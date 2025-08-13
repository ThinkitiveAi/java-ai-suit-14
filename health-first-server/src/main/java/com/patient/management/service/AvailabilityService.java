package com.patient.management.service;

import com.patient.management.dto.*;
import com.patient.management.entity.AppointmentSlot;
import com.patient.management.entity.Provider;
import com.patient.management.entity.ProviderAvailability;
import com.patient.management.repository.AppointmentSlotRepository;
import com.patient.management.repository.ProviderAvailabilityRepository;
import com.patient.management.repository.ProviderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {
    private final ProviderAvailabilityRepository availabilityRepository;
    private final AppointmentSlotRepository slotRepository;
    private final ProviderRepository providerRepository;

    private static final int MIN_SLOT_MINUTES = 5;
    private static final int MAX_SLOT_MINUTES = 480;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Transactional
    public AvailabilityResponses createAvailability(String providerId, CreateAvailabilityRequest request) {
        validateSlotDurations(request.getSlot_duration(), request.getBreak_duration());
        Provider provider = providerRepository.findById(providerId).orElseThrow(() -> new IllegalArgumentException("Provider not found"));

        LocalDate startDate = LocalDate.parse(request.getDate(), DATE_FMT);
        LocalDate endDate = request.getIs_recurring() != null && request.getIs_recurring()
                ? LocalDate.parse(request.getRecurrence_end_date(), DATE_FMT)
                : startDate;
        if (endDate.isBefore(startDate)) throw new IllegalArgumentException("recurrence_end_date cannot be before date");

        ProviderAvailability base = mapToAvailability(providerId, request, startDate);
        List<ProviderAvailability> allAvailabilities = new ArrayList<>();

        if (Boolean.TRUE.equals(request.getIs_recurring())) {
            ProviderAvailability.RecurrencePattern pattern = mapRecurrence(request.getRecurrence_pattern());
            LocalDate cursor = startDate;
            while (!cursor.isAfter(endDate)) {
                ProviderAvailability copy = cloneForDate(base, cursor);
                allAvailabilities.add(copy);
                cursor = nextDate(cursor, pattern);
            }
        } else {
            allAvailabilities.add(base);
        }

        int totalSlotsCreated = 0;
        for (ProviderAvailability availability : allAvailabilities) {
            availabilityRepository.save(availability);
            int created = generateSlotsForAvailability(availability);
            totalSlotsCreated += created;
        }

        int totalAppointmentsAvailable = totalSlotsCreated; // since max per slot defaults to 1

        AvailabilityResponses.CreateData data = new AvailabilityResponses.CreateData(
                base.getId(), totalSlotsCreated,
                new DateRangeDto(startDate.format(DATE_FMT), endDate.format(DATE_FMT)),
                totalAppointmentsAvailable
        );
        return new AvailabilityResponses(true, "Availability slots created successfully", data);
    }

    public AvailabilityResponses.ProviderAvailabilityData getProviderAvailability(String providerId, LocalDate start, LocalDate end, String statusStr, String typeStr, String tz) {
        ZoneId zone = tz != null && !tz.isBlank() ? ZoneId.of(tz) : ZoneId.systemDefault();
        AppointmentSlot.SlotStatus status = statusStr != null ? AppointmentSlot.SlotStatus.valueOf(statusStr.toUpperCase()) : null;
        ProviderAvailability.AppointmentType type = typeStr != null ? mapAppointmentType(typeStr) : null;

        Instant startInstant = start.atStartOfDay(zone).toInstant();
        Instant endInstant = end.plusDays(1).atStartOfDay(zone).toInstant();

        List<AppointmentSlot> slots = slotRepository.findByProviderAndRangeAndFilters(providerId, startInstant, endInstant, status, type);

        Map<LocalDate, List<AppointmentSlot>> byDay = slots.stream()
                .collect(Collectors.groupingBy(s -> LocalDateTime.ofInstant(s.getSlotStartTime(), zone).toLocalDate(), TreeMap::new, Collectors.toList()));

        int total = slots.size();
        int available = (int) slots.stream().filter(s -> s.getStatus() == AppointmentSlot.SlotStatus.AVAILABLE).count();
        int booked = (int) slots.stream().filter(s -> s.getStatus() == AppointmentSlot.SlotStatus.BOOKED).count();
        int cancelled = (int) slots.stream().filter(s -> s.getStatus() == AppointmentSlot.SlotStatus.CANCELLED).count();

        List<DayAvailabilityDto> days = new ArrayList<>();
        for (Map.Entry<LocalDate, List<AppointmentSlot>> entry : byDay.entrySet()) {
            List<SlotDto> slotDtos = entry.getValue().stream().sorted(Comparator.comparing(AppointmentSlot::getSlotStartTime))
                    .map(s -> new SlotDto(
                            s.getId(),
                            TIME_FMT.format(LocalDateTime.ofInstant(s.getSlotStartTime(), zone)),
                            TIME_FMT.format(LocalDateTime.ofInstant(s.getSlotEndTime(), zone)),
                            s.getStatus().name().toLowerCase(),
                            s.getAppointmentType().name().toLowerCase(),
                            null, // location optional in slot response summary
                            null  // pricing optional in slot response summary
                    ))
                    .collect(Collectors.toList());
            days.add(new DayAvailabilityDto(entry.getKey().format(DATE_FMT), slotDtos));
        }

        AvailabilitySummaryDto summary = new AvailabilitySummaryDto(total, available, booked, cancelled);
        return new AvailabilityResponses.ProviderAvailabilityData(providerId, summary, days);
    }

    @Transactional
    public void updateSlot(String slotId, UpdateAvailabilitySlotRequest request, String timezone) {
        AppointmentSlot slot = slotRepository.findById(slotId).orElseThrow(() -> new IllegalArgumentException("Slot not found"));
        ProviderAvailability availability = availabilityRepository.findById(slot.getAvailabilityId()).orElseThrow(() -> new IllegalStateException("Availability not found"));
        ZoneId zone = ZoneId.of(timezone != null ? timezone : availability.getTimezone());

        if (request.getStart_time() != null && request.getEnd_time() != null) {
            LocalDate date = availability.getDate();
            LocalTime start = LocalTime.parse(request.getStart_time(), TIME_FMT);
            LocalTime end = LocalTime.parse(request.getEnd_time(), TIME_FMT);
            if (!end.isAfter(start)) throw new IllegalArgumentException("end_time must be after start_time");
            ZonedDateTime startZdt = ZonedDateTime.of(date, start, zone);
            ZonedDateTime endZdt = ZonedDateTime.of(date, end, zone);
            Instant newStart = startZdt.toInstant();
            Instant newEnd = endZdt.toInstant();
            List<AppointmentSlot> overlaps = slotRepository.findOverlappingSlots(slot.getProviderId(), newStart, newEnd)
                    .stream().filter(s -> !s.getId().equals(slot.getId())).collect(Collectors.toList());
            if (!overlaps.isEmpty()) throw new IllegalArgumentException("Slot time overlaps with existing slot");
            slot.setSlotStartTime(newStart);
            slot.setSlotEndTime(newEnd);
        }
        if (request.getStatus() != null) {
            AppointmentSlot.SlotStatus newStatus = AppointmentSlot.SlotStatus.valueOf(request.getStatus().toUpperCase());
            slot.setStatus(newStatus);
        }
        if (request.getPricing() != null && request.getPricing().getBase_fee() != null) {
            // Update underlying availability pricing if present
            ProviderAvailability.Pricing pricing = availability.getPricing();
            if (pricing == null) {
                pricing = new ProviderAvailability.Pricing();
                availability.setPricing(pricing);
            }
            pricing.setBaseFee(request.getPricing().getBase_fee());
            availabilityRepository.save(availability);
        }
        if (request.getNotes() != null) {
            availability.setNotes(request.getNotes());
            availabilityRepository.save(availability);
        }
        slotRepository.save(slot);
    }

    @Transactional
    public void deleteSlot(String slotId, boolean deleteRecurring, String reason) {
        AppointmentSlot slot = slotRepository.findById(slotId).orElseThrow(() -> new IllegalArgumentException("Slot not found"));
        if (slot.getStatus() == AppointmentSlot.SlotStatus.BOOKED) {
            throw new IllegalStateException("Cannot delete a booked slot. Please cancel the appointment first.");
        }
        if (deleteRecurring) {
            List<AppointmentSlot> siblings = slotRepository.findByAvailabilityId(slot.getAvailabilityId());
            for (AppointmentSlot s : siblings) {
                if (s.getStatus() == AppointmentSlot.SlotStatus.BOOKED) continue;
                s.setStatus(AppointmentSlot.SlotStatus.CANCELLED);
            }
            slotRepository.saveAll(siblings);
        } else {
            slot.setStatus(AppointmentSlot.SlotStatus.CANCELLED);
            slotRepository.save(slot);
        }
    }

    public SearchAvailabilityResponse search(String date, String startDate, String endDate, String specialization, String location,
                                             String appointmentType, Boolean insuranceAccepted, BigDecimal maxPrice, String timezone, Boolean availableOnly) {
        ZoneId zone = timezone != null && !timezone.isBlank() ? ZoneId.of(timezone) : ZoneId.systemDefault();
        Instant startInstant;
        Instant endInstant;
        if (date != null) {
            LocalDate d = LocalDate.parse(date, DATE_FMT);
            startInstant = d.atStartOfDay(zone).toInstant();
            endInstant = d.plusDays(1).atStartOfDay(zone).toInstant();
        } else {
            LocalDate start = LocalDate.parse(startDate, DATE_FMT);
            LocalDate end = LocalDate.parse(endDate, DATE_FMT);
            startInstant = start.atStartOfDay(zone).toInstant();
            endInstant = end.plusDays(1).atStartOfDay(zone).toInstant();
        }

        List<AppointmentSlot> slots = slotRepository.findInRange(startInstant, endInstant, availableOnly != null ? availableOnly : true);

        // Filter by provider data, pricing, appointment type
        List<SearchResultDto> results = new ArrayList<>();
        Map<String, List<AppointmentSlot>> byProvider = slots.stream().collect(Collectors.groupingBy(AppointmentSlot::getProviderId));
        for (Map.Entry<String, List<AppointmentSlot>> entry : byProvider.entrySet()) {
            Optional<Provider> providerOpt = providerRepository.findById(entry.getKey());
            if (providerOpt.isEmpty()) continue;
            Provider prov = providerOpt.get();
            if (specialization != null && !specialization.isBlank() && !prov.getSpecialization().equalsIgnoreCase(specialization)) continue;
            String clinicAddress = prov.getClinicAddress() != null ? prov.getClinicAddress().getStreet() + ", " + prov.getClinicAddress().getCity() + ", " + prov.getClinicAddress().getState() : null;
            if (location != null && clinicAddress != null && !clinicAddress.toLowerCase().contains(location.toLowerCase())) continue;

            List<AppointmentSlot> providerSlots = entry.getValue();
            List<AvailableSlotDto> availableSlotDtos = new ArrayList<>();
            for (AppointmentSlot s : providerSlots) {
                if (appointmentType != null && !s.getAppointmentType().name().equalsIgnoreCase(appointmentType)) continue;
                ProviderAvailability availability = availabilityRepository.findById(s.getAvailabilityId()).orElse(null);
                if (availability == null) continue;
                if (insuranceAccepted != null && availability.getPricing() != null && availability.getPricing().getInsuranceAccepted() != null) {
                    if (!availability.getPricing().getInsuranceAccepted().equals(insuranceAccepted)) continue;
                }
                if (maxPrice != null && availability.getPricing() != null && availability.getPricing().getBaseFee() != null) {
                    if (availability.getPricing().getBaseFee().compareTo(maxPrice) > 0) continue;
                }
                LocalDateTime startLdt = LocalDateTime.ofInstant(s.getSlotStartTime(), zone);
                LocalDate dateL = startLdt.toLocalDate();
                String startStr = TIME_FMT.format(startLdt);
                String endStr = TIME_FMT.format(LocalDateTime.ofInstant(s.getSlotEndTime(), zone));

                LocationDto loc = null;
                if (availability.getLocation() != null) {
                    loc = new LocationDto(
                            availability.getLocation().getType().name().toLowerCase(),
                            availability.getLocation().getAddress(),
                            availability.getLocation().getRoomNumber()
                    );
                }
                PricingDto price = null;
                if (availability.getPricing() != null) {
                    price = new PricingDto(
                            availability.getPricing().getBaseFee(),
                            availability.getPricing().getInsuranceAccepted(),
                            availability.getPricing().getCurrency()
                    );
                }
                AvailableSlotDto dto = new AvailableSlotDto(
                        s.getId(), dateL.format(DATE_FMT), startStr, endStr,
                        s.getAppointmentType().name().toLowerCase(),
                        loc,
                        price,
                        availability.getSpecialRequirements()
                );
                availableSlotDtos.add(dto);
            }
            if (availableSlotDtos.isEmpty()) continue;
            ProviderInfoDto pinfo = new ProviderInfoDto(
                    prov.getId(), prov.getFirstName() + " " + prov.getLastName(), prov.getSpecialization(), prov.getYearsOfExperience(), 4.8,
                    clinicAddress
            );
            results.add(new SearchResultDto(pinfo, availableSlotDtos));
        }

        SearchCriteriaDto criteria = new SearchCriteriaDto(
                date,
                startDate,
                endDate,
                specialization,
                location,
                appointmentType,
                insuranceAccepted,
                maxPrice,
                timezone,
                availableOnly
        );
        SearchDataPayloadDto data = new SearchDataPayloadDto(criteria, results.size(), results);
        return new SearchAvailabilityResponse(true, data);
    }

    @Transactional
    public String bookAppointment(BookAppointmentRequest request) {
        AppointmentSlot slot = slotRepository.findById(request.getSlot_id()).orElseThrow(() -> new IllegalArgumentException("Slot not found"));
        if (slot.getStatus() != AppointmentSlot.SlotStatus.AVAILABLE) throw new IllegalStateException("Slot is not available");
        slot.setStatus(AppointmentSlot.SlotStatus.BOOKED);
        slot.setPatientId(request.getPatient_id());
        slot.setBookingReference(UUID.randomUUID().toString());
        slotRepository.save(slot);
        return slot.getBookingReference();
    }

    private void validateSlotDurations(Integer slotDuration, Integer breakDuration) {
        int sd = slotDuration != null ? slotDuration : 30;
        int bd = breakDuration != null ? breakDuration : 0;
        if (sd < MIN_SLOT_MINUTES || sd > MAX_SLOT_MINUTES) throw new IllegalArgumentException("slot_duration out of range");
        if (bd < 0 || bd > 120) throw new IllegalArgumentException("break_duration out of range");
    }

    private ProviderAvailability mapToAvailability(String providerId, CreateAvailabilityRequest req, LocalDate date) {
        ProviderAvailability availability = new ProviderAvailability();
        availability.setId(UUID.randomUUID().toString());
        availability.setProviderId(providerId);
        availability.setDate(date);
        availability.setTimezone(req.getTimezone());
        availability.setStartTime(req.getStart_time());
        availability.setEndTime(req.getEnd_time());
        availability.setRecurring(Boolean.TRUE.equals(req.getIs_recurring()));
        if (req.getRecurrence_pattern() != null) {
            availability.setRecurrencePattern(mapRecurrence(req.getRecurrence_pattern()));
        }
        if (req.getRecurrence_end_date() != null && !req.getRecurrence_end_date().isBlank()) {
            availability.setRecurrenceEndDate(LocalDate.parse(req.getRecurrence_end_date(), DATE_FMT));
        }
        if (req.getSlot_duration() != null) availability.setSlotDuration(req.getSlot_duration());
        if (req.getBreak_duration() != null) availability.setBreakDuration(req.getBreak_duration());
        if (req.getAppointment_type() != null) availability.setAppointmentType(mapAppointmentType(req.getAppointment_type()));
        if (req.getLocation() != null) {
            ProviderAvailability.Location loc = new ProviderAvailability.Location();
            loc.setType(mapLocationType(req.getLocation().getType()));
            loc.setAddress(req.getLocation().getAddress());
            loc.setRoomNumber(req.getLocation().getRoom_number());
            availability.setLocation(loc);
        }
        if (req.getPricing() != null) {
            ProviderAvailability.Pricing p = new ProviderAvailability.Pricing();
            p.setBaseFee(req.getPricing().getBase_fee());
            p.setInsuranceAccepted(req.getPricing().getInsurance_accepted());
            p.setCurrency(req.getPricing().getCurrency() != null ? req.getPricing().getCurrency() : "USD");
            availability.setPricing(p);
        }
        availability.setNotes(req.getNotes());
        availability.setSpecialRequirements(req.getSpecial_requirements());
        return availability;
    }

    private ProviderAvailability cloneForDate(ProviderAvailability base, LocalDate date) {
        ProviderAvailability copy = new ProviderAvailability();
        copy.setId(UUID.randomUUID().toString());
        copy.setProviderId(base.getProviderId());
        copy.setDate(date);
        copy.setTimezone(base.getTimezone());
        copy.setStartTime(base.getStartTime());
        copy.setEndTime(base.getEndTime());
        copy.setRecurring(base.isRecurring());
        copy.setRecurrencePattern(base.getRecurrencePattern());
        copy.setRecurrenceEndDate(base.getRecurrenceEndDate());
        copy.setSlotDuration(base.getSlotDuration());
        copy.setBreakDuration(base.getBreakDuration());
        copy.setStatus(base.getStatus());
        copy.setMaxAppointmentsPerSlot(base.getMaxAppointmentsPerSlot());
        copy.setCurrentAppointments(0);
        copy.setAppointmentType(base.getAppointmentType());
        copy.setLocation(base.getLocation());
        copy.setPricing(base.getPricing());
        copy.setNotes(base.getNotes());
        copy.setSpecialRequirements(base.getSpecialRequirements());
        return copy;
    }

    private LocalDate nextDate(LocalDate d, ProviderAvailability.RecurrencePattern pattern) {
        return switch (pattern) {
            case DAILY -> d.plusDays(1);
            case WEEKLY -> d.plusWeeks(1);
            case MONTHLY -> d.plusMonths(1);
        };
    }

    private int generateSlotsForAvailability(ProviderAvailability availability) {
        ZoneId zone = ZoneId.of(availability.getTimezone());
        LocalDate date = availability.getDate();
        LocalTime start = LocalTime.parse(availability.getStartTime(), TIME_FMT);
        LocalTime end = LocalTime.parse(availability.getEndTime(), TIME_FMT);
        if (!end.isAfter(start)) throw new IllegalArgumentException("end_time must be after start_time");
        int duration = availability.getSlotDuration();
        int breakMin = availability.getBreakDuration();

        int count = 0;
        LocalTime cursor = start;
        while (cursor.plusMinutes(duration).compareTo(end) <= 0) {
            LocalTime slotEndLocal = cursor.plusMinutes(duration);
            ZonedDateTime slotStartZdt = ZonedDateTime.of(date, cursor, zone);
            ZonedDateTime slotEndZdt = ZonedDateTime.of(date, slotEndLocal, zone);
            Instant slotStartUtc = slotStartZdt.toInstant();
            Instant slotEndUtc = slotEndZdt.toInstant();
            // prevent overlap
            List<AppointmentSlot> overlaps = slotRepository.findOverlappingSlots(availability.getProviderId(), slotStartUtc, slotEndUtc);
            if (overlaps.isEmpty()) {
                AppointmentSlot slot = AppointmentSlot.builder()
                        .id(UUID.randomUUID().toString())
                        .availabilityId(availability.getId())
                        .providerId(availability.getProviderId())
                        .slotStartTime(slotStartUtc)
                        .slotEndTime(slotEndUtc)
                        .appointmentType(availability.getAppointmentType())
                        .status(AppointmentSlot.SlotStatus.AVAILABLE)
                        .build();
                slotRepository.save(slot);
                count++;
            }
            cursor = slotEndLocal.plusMinutes(breakMin);
        }
        return count;
    }

    private ProviderAvailability.RecurrencePattern mapRecurrence(String val) {
        return switch (val.toLowerCase()) {
            case "daily" -> ProviderAvailability.RecurrencePattern.DAILY;
            case "weekly" -> ProviderAvailability.RecurrencePattern.WEEKLY;
            case "monthly" -> ProviderAvailability.RecurrencePattern.MONTHLY;
            default -> throw new IllegalArgumentException("Invalid recurrence_pattern");
        };
    }

    private ProviderAvailability.LocationType mapLocationType(String val) {
        return switch (val.toLowerCase()) {
            case "clinic" -> ProviderAvailability.LocationType.CLINIC;
            case "hospital" -> ProviderAvailability.LocationType.HOSPITAL;
            case "telemedicine" -> ProviderAvailability.LocationType.TELEMEDICINE;
            case "home_visit" -> ProviderAvailability.LocationType.HOME_VISIT;
            default -> throw new IllegalArgumentException("Invalid location.type");
        };
    }

    private ProviderAvailability.AppointmentType mapAppointmentType(String val) {
        return switch (val.toLowerCase()) {
            case "consultation" -> ProviderAvailability.AppointmentType.CONSULTATION;
            case "follow_up" -> ProviderAvailability.AppointmentType.FOLLOW_UP;
            case "emergency" -> ProviderAvailability.AppointmentType.EMERGENCY;
            case "telemedicine" -> ProviderAvailability.AppointmentType.TELEMEDICINE;
            default -> throw new IllegalArgumentException("Invalid appointment_type");
        };
    }
} 
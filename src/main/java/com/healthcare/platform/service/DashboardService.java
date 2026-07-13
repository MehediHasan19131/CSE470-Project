package com.healthcare.platform.service;

import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.*;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final UserRepository users;
    private final AppointmentRepository appointments;
    private final MedicineRepository medicines;
    private final RatingRepository ratings;

    public DashboardService(UserRepository users, AppointmentRepository appointments,
                            MedicineRepository medicines, RatingRepository ratings) {
        this.users = users;
        this.appointments = appointments;
        this.medicines = medicines;
        this.ratings = ratings;
    }

    public Map<String, Object> dashboard(User user) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalPatients", users.findByRole(UserRole.PATIENT).size());
        data.put("totalDoctors", users.findByRole(UserRole.DOCTOR).size());
        data.put("totalHospitals", users.findByRole(UserRole.HOSPITAL).size());
        data.put("totalPharmacies", users.findByRole(UserRole.PHARMACY).size());
        data.put("totalAppointments", appointments.count());
        data.put("totalMedicines", medicines.count());
        return data;
    }
}

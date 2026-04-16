package com.example.carrentingapp.repositories;

import com.example.carrentingapp.models.Car;
import com.example.carrentingapp.utils.Constants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CarRepository {
    public interface CarListCallback {
        void onSuccess(List<Car> cars);
        void onFailure(String error);
    }
    public interface CarCallback {
        void onSuccess(Car car);
        void onFailure(String error);
    }
    public interface ActionCallback {
        void onSuccess();
        void onFailure(String error);
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // FR4.2 - Load home cars (latest 20)
    public void getCars(DocumentSnapshot lastDoc, CarListCallback cb) {
        Query q = db.collection(Constants.COLLECTION_CARS)
            .whereEqualTo("available", true)
            .limit(Constants.PAGE_SIZE);
        if (lastDoc != null) q = q.startAfter(lastDoc);
        q.get().addOnSuccessListener(snap -> {
            List<Car> list = new ArrayList<>();
            for (QueryDocumentSnapshot d : snap) {
                Car car = d.toObject(Car.class);
                car.setId(d.getId());
                list.add(car);
            }
            // Client-side sort to avoid index requirement
            list.sort((c1, c2) -> Long.compare(c2.getCreatedAt(), c1.getCreatedAt()));
            cb.onSuccess(list);
        }).addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    // FR3.1 - Search by location or type
    public void searchCars(String query, String type, String brand, double minPrice, double maxPrice, CarListCallback cb) {
        Query q = db.collection(Constants.COLLECTION_CARS)
            .whereEqualTo("available", true)
            .limit(Constants.PAGE_SIZE);

        if (type != null && !type.isEmpty()) q = db.collection(Constants.COLLECTION_CARS)
            .whereEqualTo("available", true)
            .whereEqualTo("type", type)
            .limit(Constants.PAGE_SIZE);

        q.get().addOnSuccessListener(snap -> {
            List<Car> list = new ArrayList<>();
            for (QueryDocumentSnapshot d : snap) {
                Car car = d.toObject(Car.class);
                car.setId(d.getId());
                // Client-side filtering for location & price
                boolean matchLocation = query == null || query.isEmpty() ||
                    (car.getLocation() != null && car.getLocation().toLowerCase().contains(query.toLowerCase())) ||
                    (car.getName() != null && car.getName().toLowerCase().contains(query.toLowerCase()));
                boolean matchPrice = (minPrice <= 0 || car.getPricePerDay() >= minPrice) &&
                    (maxPrice <= 0 || car.getPricePerDay() <= maxPrice);
                boolean matchBrand = brand == null || brand.isEmpty() ||
                    (car.getBrand() != null && car.getBrand().equalsIgnoreCase(brand));
                if (matchLocation && matchPrice && matchBrand) list.add(car);
            }
            // Client-side sort
            list.sort((c1, c2) -> Long.compare(c2.getCreatedAt(), c1.getCreatedAt()));
            cb.onSuccess(list);
        }).addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    // FR5.1 - Get car detail
    public void getCarById(String carId, CarCallback cb) {
        db.collection(Constants.COLLECTION_CARS).document(carId).get()
            .addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    Car car = doc.toObject(Car.class);
                    if (car != null) { car.setId(doc.getId()); cb.onSuccess(car); }
                    else cb.onFailure("Không tìm thấy xe");
                } else cb.onFailure("Xe không còn tồn tại");
            })
            .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    // FR2.1 - Post a car
    public void addCar(Car car, ActionCallback cb) {
        car.setCreatedAt(System.currentTimeMillis());
        car.setAvailable(true);
        db.collection(Constants.COLLECTION_CARS).add(car)
            .addOnSuccessListener(ref -> { car.setId(ref.getId()); cb.onSuccess(); })
            .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    // FR6.2 - Edit car
    public void updateCar(Car car, ActionCallback cb) {
        db.collection(Constants.COLLECTION_CARS).document(car.getId()).set(car)
            .addOnSuccessListener(v -> cb.onSuccess())
            .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    // FR6.5 - Delete car
    public void deleteCar(String carId, ActionCallback cb) {
        db.collection(Constants.COLLECTION_CARS).document(carId).delete()
            .addOnSuccessListener(v -> cb.onSuccess())
            .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    // FR6.1 - Get cars by owner
    public void getCarsByOwner(String ownerId, CarListCallback cb) {
        db.collection(Constants.COLLECTION_CARS)
            .whereEqualTo("ownerId", ownerId)
            .get()
            .addOnSuccessListener(snap -> {
                List<Car> list = new ArrayList<>();
                for (QueryDocumentSnapshot d : snap) {
                    Car car = d.toObject(Car.class); car.setId(d.getId()); list.add(car);
                }
                // Client-side sort
                list.sort((c1, c2) -> Long.compare(c2.getCreatedAt(), c1.getCreatedAt()));
                cb.onSuccess(list);
            })
            .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    // Add sample data for demonstration
    public void seedSampleData() {
        String ownerId = "system_seed";
        List<Car> samples = new ArrayList<>();

        Car c1 = new Car();
        c1.setName("Honda City 2023"); c1.setBrand("Honda"); c1.setType(Constants.TYPE_CAR);
        c1.setPricePerDay(700000); c1.setLocation("Hà Nội"); c1.setAvailable(true);
        c1.setOwnerId(ownerId); c1.setCondition(Constants.CONDITION_NEW);
        c1.setCreatedAt(System.currentTimeMillis());
        c1.setImageUrls(List.of("https://images.unsplash.com/photo-1549317661-bd32c8ce0db2"));
        samples.add(c1);

        Car c2 = new Car();
        c2.setName("Toyota Fortuner"); c2.setBrand("Toyota"); c2.setType(Constants.TYPE_SUV);
        c2.setPricePerDay(1200000); c2.setLocation("TP. HCM"); c2.setAvailable(true);
        c2.setOwnerId(ownerId); c2.setCondition(Constants.CONDITION_GOOD);
        c2.setCreatedAt(System.currentTimeMillis() - 10000);
        c2.setImageUrls(List.of("https://images.unsplash.com/photo-1533473359331-0135ef1b58bf"));
        samples.add(c2);

        Car c3 = new Car();
        c3.setName("VinFast VF8"); c3.setBrand("VinFast"); c3.setType(Constants.TYPE_ELECTRIC);
        c3.setPricePerDay(1500000); c3.setLocation("Đà Nẵng"); c3.setAvailable(true);
        c3.setOwnerId(ownerId); c3.setCondition(Constants.CONDITION_NEW);
        c3.setCreatedAt(System.currentTimeMillis() - 20000);
        c3.setImageUrls(List.of("https://images.unsplash.com/photo-1617788138017-80ad40651399"));
        samples.add(c3);

        Car c4 = new Car();
        c4.setName("Honda SH 150i"); c4.setBrand("Honda"); c4.setType(Constants.TYPE_MOTORBIKE);
        c4.setPricePerDay(250000); c4.setLocation("Hà Nội"); c4.setAvailable(true);
        c4.setOwnerId(ownerId); c4.setCondition(Constants.CONDITION_NEW);
        c4.setCreatedAt(System.currentTimeMillis() - 30000);
        c4.setImageUrls(List.of("https://images.unsplash.com/photo-1558981403-c5f91cbba527"));
        samples.add(c4);

        for (Car c : samples) {
            db.collection(Constants.COLLECTION_CARS).add(c);
        }
    }
}

# Menggunakan Java 21 sesuai build.gradle
FROM eclipse-temurin:21-jre
WORKDIR /app

# Mengaktifkan profil 'production' untuk Spring Boot
ENV SPRING_PROFILES_ACTIVE=production

# Menyalin file .jar yang akan dihasilkan oleh Gradle
COPY build/libs/*.jar app.jar

# Port yang diekspos oleh aplikasi Spring Boot
EXPOSE 8080

# Perintah untuk menjalankan aplikasi
ENTRYPOINT ["java", "-jar", "app.jar"]
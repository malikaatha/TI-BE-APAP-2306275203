# Tugas Individu 1 - Deployment

**Nama:** Malika Atha Indurasmi
**NPM:** 2306275203 
**Kelas:** C

---

## 1. Bukti Deployment

### Screenshot Tugas Individu (Spring Boot & Vue)
**Backend (Spring Boot):**
![alt text](<Screenshot 2025-11-23 155842.png>)

**Frontend (Vue):**
![alt text](<Screenshot 2025-11-23 160113.png>)

---

## 2. CI/CD Pipeline (Current Implementation)

Pipeline berjalan otomatis ketika ada `push` ke branch `main`.
1.  **Build & Push:** GitHub Actions mem-build Docker Image dari source code, memasukkan variabel environment (seperti Database URL) sebagai `build-arg`, lalu men-push image tersebut ke **Docker Hub**.
2.  **Deploy:** Runner melakukan SSH ke server EC2. Di sana, runner mengunduh file konfigurasi Kubernetes (`deployment.yaml`, `service.yaml`, `ingress.yaml`) yang terbaru, mengganti tag image dengan hash commit terbaru, lalu menjalankan perintah `kubectl apply` untuk memperbarui aplikasi di cluster K3s.

---

## 3. CI/CD Pipeline Improvement

Pipeline saat ini langsung men-deploy ke production tanpa pengecekan kualitas kode yang ketat. Perbaikan yang dapat dilakukan:
1.  **Unit Testing & Linting:** Menambahkan stage `Test` sebelum build docker untuk memastikan tidak ada kode yang error atau bug logika.
2.  **Code Quality Analysis:** Mengintegrasikan **SonarQube** untuk mengecek kualitas kode, security vulnerability, dan code smells.
3.  **Staging Environment:** Sebelum ke production, deploy dulu ke environment `staging` untuk dites manual (UAT).
4.  **Manual Approval:** Menambahkan tombol "Approve" sebelum deploy ke Production agar deployment lebih terkontrol.

---

## 4. Elastic IP pada EC2

**Mengapa harus mengaitkan dengan Elastic IP?**
Secara default, jika instance EC2 dimatikan (stop) dan dinyalakan kembali (start), AWS akan memberikan **Public IP baru** yang acak. Hal ini akan memutuskan koneksi DNS (domain `hafizmuh.site`) yang sudah kita setting ke IP lama. Dengan **Elastic IP**, kita mendapatkan alamat IP statis yang tidak akan berubah meskipun server di-restart, sehingga domain akan selalu mengarah ke server yang benar.

**Apa yang terjadi jika tidak mengaitkan?**
Setiap kali sesi AWS Academy habis atau server di-restart, IP Public akan berubah. Kita harus terus-menerus mengupdate konfigurasi DNS (A Record) dan konfigurasi di GitLab/GitHub Secrets (`EC2_HOST`) agar deployment dan akses website bisa berjalan kembali. Hal ini sangat tidak efisien.

---

## 5. Perbedaan Docker dan Kubernetes pada Praktikum Ini

*   **Docker:** Pada praktikum ini, Docker berperan sebagai **Container Engine** dan **Packaging Tool**. Kita menggunakan Docker untuk membungkus aplikasi (Spring Boot/Vue) beserta dependensinya menjadi sebuah *Image* agar bisa berjalan di mana saja. Selain itu, Docker digunakan secara langsung (tanpa orkestrasi) untuk menjalankan Database PostgreSQL.
*   **Kubernetes (K3s):** Berperan sebagai **Container Orchestrator**. K3s bertugas mengelola container aplikasi (Pod), mengatur replika, menjaga aplikasi tetap hidup (self-healing), serta mengatur jaringan (Service & Ingress) agar aplikasi backend dan frontend bisa saling berkomunikasi dan diakses dari luar.

---

## 6. Bagian Pipeline Paling Penting

Menurut saya, bagian yang paling penting adalah **Build & Push Docker Image**.
Tahap ini adalah inti dari konsep *Containerization*. Di tahap ini, kode aplikasi "dibekukan" bersama semua dependensinya menjadi sebuah *artifact* (Image) yang immutabel (tidak berubah). Jika tahap ini sukses, kita memiliki jaminan bahwa aplikasi tersebut bisa dijalankan di server mana pun (Local, EC2, atau server lain) dengan hasil yang sama persis. Tanpa image yang valid, tahap deployment ke Kubernetes tidak akan mungkin dilakukan. Selain itu, di tahap ini kita juga menyuntikkan konfigurasi penting (seperti `VITE_API_URL` pada frontend) agar aplikasi tahu kemana harus berkomunikasi.

---

## 7. Kegunaan File Konfigurasi Kubernetes

Berikut adalah penjelasan kegunaan 5 file konfigurasi tersebut:

1.  **`k8s/deployment.yaml`**: File ini mendefinisikan "blueprint" aplikasi. Ia mengatur image apa yang dipakai, berapa jumlah replika (pod) yang harus jalan, port container yang dibuka, serta environment variable yang dibutuhkan aplikasi. Ini adalah komponen utama yang menjaga aplikasi tetap *running*.
2.  **`k8s/service.yaml`**: Berfungsi sebagai penghubung jaringan internal. Ia memberikan IP stabil dan nama DNS internal untuk sekumpulan Pod. Dengan ini, komponen lain (seperti Ingress) bisa berkomunikasi dengan aplikasi tanpa peduli IP Pod yang berubah-ubah.
3.  **`k8s/ingress.yaml`**: Berperan sebagai "Gerbang Utama" (Router) dari dunia luar. Ia mengatur aturan domain (misal: `hafizmuh.site`) dan meneruskan request HTTP/HTTPS dari user ke `service` yang tepat di dalam cluster.
4.  **`secret.yaml` (Generated in CI)**: Digunakan untuk menyimpan data sensitif seperti **Password Database** atau **API Keys**. Kubernetes mengenkripsi data ini (base64 encoded secara default) dan menyuntikkannya ke dalam aplikasi secara aman, sehingga password tidak perlu ditulis mentah (hardcoded) di dalam kode.
5.  **`config.yaml` / ConfigMap (Generated in CI)**: Digunakan untuk menyimpan data konfigurasi yang **tidak sensitif**, seperti URL Database (`JDBC URL`) atau nama user database. Ini memisahkan konfigurasi dari kode aplikasi, memudahkan perubahan setting tanpa perlu build ulang image.

---

## 8. Mekanisme Start on Restart

Konsep *start on restart* memastikan layanan kembali menyala otomatis setelah server mati atau crash.

1.  **Docker (Database):**
    Diterapkan menggunakan flag `--restart=always` saat menjalankan container (atau di `docker-compose`).
    *   *Cara kerja:* Daemon Docker memantau status container. Jika container mati (crash) atau daemon Docker baru menyala (setelah reboot server), Docker akan otomatis menjalankan kembali container tersebut.
2.  **Kubernetes (Aplikasi BE/FE):**
    Diterapkan melalui konsep **Desired State** pada `Deployment` dan **Systemd** pada K3s.
    *   *Cara kerja:* Service K3s diatur untuk menyala otomatis saat booting OS (`systemctl enable --now k3s`). Setelah K3s menyala, ia membaca konfigurasi terakhir. Jika Deployment meminta 1 replika tapi tidak ada Pod yang jalan, Kubernetes Scheduler akan segera menjadwalkan pembuatan Pod baru untuk memenuhi status yang diinginkan tersebut.

---

## 9. Keuntungan Kubernetes vs Docker Run

Keuntungan utama menggunakan Kubernetes dibandingkan menjalankan `docker run` manual adalah **Orkestrasi dan Otomatisasi (Self-Healing & Scaling)**.

*   **Self-Healing:** Jika aplikasi crash, Kubernetes otomatis me-restart Pod tersebut. Jika node server rusak, Kubernetes memindahkan Pod ke node lain. Di Docker biasa, kita harus monitoring dan restart manual.
*   **Zero Downtime Deployment:** Kubernetes mendukung *Rolling Update*, di mana versi baru aplikasi dinyalakan perlahan menggantikan versi lama tanpa memutus koneksi user.
*   **Service Discovery & Load Balancing:** Kubernetes otomatis mengatur jaringan antar layanan lewat Service dan Ingress, memudahkan komunikasi antar microservices tanpa pusing memikirkan IP Address.

---

## 10. Tipe Service Kubernetes: ClusterIP, NodePort, LoadBalancer

1.  **ClusterIP (Default):** Memberikan IP internal yang hanya bisa diakses **dari dalam** cluster Kubernetes. Tidak bisa diakses langsung dari internet.
2.  **NodePort:** Membuka port tertentu pada setiap Node (server) fisik. Aplikasi bisa diakses via `IP_Server:Port`.
3.  **LoadBalancer:** Meminta Cloud Provider (seperti AWS/GCP) untuk menyediakan Load Balancer eksternal (dan IP Public khusus) yang mengarahkan trafik ke service.

**Mengapa ClusterIP sesuai untuk praktikum ini?**
Karena kita menggunakan **Ingress Controller (Traefik)**. Ingress bertindak sebagai pintu masuk tunggal (Reverse Proxy) yang menghadapi internet. Traffic masuk ke Ingress, lalu Ingress meneruskannya ke Service aplikasi. Oleh karena itu, Service aplikasi backend/frontend cukup menggunakan **ClusterIP** karena hanya perlu diakses oleh Ingress (yang berada di dalam cluster), tidak perlu diekspos langsung ke publik satu per satu. Ini lebih aman dan hemat resource.

---

## 11. Pelajaran Terpenting & Penerapan Konsep CI/CD

**Pelajaran Terpenting:**
Pelajaran paling berharga adalah pemahaman tentang **Automated Consistency**. Dengan CI/CD dan Containerization, masalah klasik *"It works on my machine but not on server"* dapat dihilangkan. Saya belajar bahwa konfigurasi environment (Secrets, ConfigMap, Docker Args) sama pentingnya dengan kode aplikasi itu sendiri.

**Penerapan di Proyek Lain:**
Konsep CI/CD ini bisa diterapkan pada proyek pengembangan aplikasi mobile atau web lainnya.
*   Contoh: Pada proyek tim (PPL), setiap kali anggota tim melakukan Push/Merge Request, pipeline otomatis menjalankan Unit Test. Jika lulus, aplikasi otomatis di-deploy ke server Staging untuk direview oleh Product Owner. Ini mempercepat siklus feedback dan mengurangi risiko error saat rilis ke user.
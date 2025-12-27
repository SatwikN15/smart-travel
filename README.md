
# Smart Travel & Commute Manager

A **comprehensive full-stack travel management web application** that helps users plan, track, and optimize their daily commutes while monitoring costs and environmental impact.

---

## 🌟 Features

### Core Features

* ➕ **Add Routes** – Create custom routes with multiple transport modes (car, bus, train, bike, walk)
* 🗺️ **Route Management** – View all your routes with distance, duration, and cost details
* ⭐ **Favorites** – Mark frequently used routes for quick access
* 📅 **Schedule** – Set up recurring commutes (daily, weekdays, weekends) with specific times
* 📊 **Statistics Dashboard** – Track total routes, total distance, commuting cost, and time
* 🌱 **Carbon Footprint** – Automatic calculation of monthly CO₂ emissions based on transport mode

### Key Highlights

* **Persistent Storage** – All data saves automatically and persists between sessions
* **Modern UI** – Clean, responsive design with smooth animations
* **Multi-Modal Transport** – Supports 5 different transportation methods
* **Cost Tracking** – Monitor your commuting expenses
* **Environmental Impact** – See the carbon footprint of your travel choices

---

## 📌 How to Use

1. **Add a New Route** – Fill in the form with start, destination, mode, distance, duration, and cost
2. **Manage Routes** – View, edit, delete, and mark favorites for quick access
3. **Track Statistics** – Check your dashboard for total distance, time, cost, and carbon footprint
4. **Schedule Recurring Commutes** – Set up recurring routes to automate planning
5. **Carbon Footprint Calculation** – Automatic estimates based on your transport choices

---

## 🚀 Technologies Used

* **Frontend:** HTML, CSS, JavaScript
* **Persistent Storage:** LocalStorage / IndexedDB / (or your backend DB if implemented)
* **Charts & Analytics:** Chart.js (optional)
* **Modern UI:** Responsive design with smooth animations

---

## 📊 Carbon Emission Estimates (Sample)

| Transport Mode | CO₂ per km (kg) |
| -------------- | --------------- |
| Car            | 0.271           |
| Bus            | 0.105           |
| Train          | 0.041           |
| Bike           | 0               |
| Walk           | 0               |

The app calculates monthly CO₂ emissions assuming typical commuting patterns.

---

## 📁 Folder Structure

```
smart-travel-commute-manager/
│── index.html
│── style.css
│── script.js
│── assets/
│   └── images/
│── README.md
```

---

## 🔧 Installation & Usage

1. Clone the repository:

```bash
git clone <your-repo-url>
```

2. Open `index.html` in any modern browser
3. Start adding routes and managing your commute

> No backend required if using LocalStorage; all data persists in the browser.

---

## 🎯 Future Enhancements

* Integration with Google Maps API for distance & route suggestions
* Mobile app version with push notifications for commute schedules
* Backend server for cloud storage and multi-device sync
* Real-time analytics and route optimization

---

## ⚡ License

This project is **open-source** and available under the [MIT License](LICENSE).

---

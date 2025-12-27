// frontend/script.js - Connects to Java Spring Boot Backend

const API_URL = 'http://localhost:8080/api';

let routes = [];
let schedules = [];
let selectedMode = 'car';

const modeEmojis = {
    car: '🚗',
    bus: '🚌',
    train: '🚆',
    bike: '🚴',
    walk: '🚶'
};

const modeNames = {
    car: 'Car',
    bus: 'Bus',
    train: 'Train',
    bike: 'Bicycle',
    walk: 'Walking'
};

// Fetch all data on load
async function loadData() {
    try {
        await Promise.all([fetchRoutes(), fetchSchedules(), fetchStatistics()]);
    } catch (error) {
        console.error('Error loading data:', error);
        showNotification('Error loading data from server', 'error');
    }
}

// Fetch routes
async function fetchRoutes() {
    try {
        const response = await fetch(`${API_URL}/routes`);
        if (!response.ok) throw new Error('Failed to fetch routes');
        routes = await response.json();
        renderRoutes();
        updateScheduleDropdown();
    } catch (error) {
        console.error('Error fetching routes:', error);
    }
}

// Fetch schedules
async function fetchSchedules() {
    try {
        const response = await fetch(`${API_URL}/schedules`);
        if (!response.ok) throw new Error('Failed to fetch schedules');
        schedules = await response.json();
        renderSchedules();
    } catch (error) {
        console.error('Error fetching schedules:', error);
    }
}

// Fetch statistics
async function fetchStatistics() {
    try {
        const response = await fetch(`${API_URL}/routes/statistics`);
        if (!response.ok) throw new Error('Failed to fetch statistics');
        const stats = await response.json();
        updateStatisticsDisplay(stats);
    } catch (error) {
        console.error('Error fetching statistics:', error);
    }
}

// Update statistics display
function updateStatisticsDisplay(stats) {
    document.getElementById('totalRoutes').textContent = stats.totalRoutes;
    document.getElementById('totalDistance').textContent = stats.totalDistance.toFixed(1);
    document.getElementById('totalCost').textContent = '₹' + stats.totalCost.toFixed(0);
    document.getElementById('totalTime').textContent = stats.totalTime.toFixed(1) + 'h';
    document.getElementById('carbonFootprint').textContent = stats.carbonFootprint.toFixed(1) + ' kg CO₂';
}

// Transport mode selection
document.querySelectorAll('.mode-btn').forEach(btn => {
    btn.addEventListener('click', function() {
        document.querySelectorAll('.mode-btn').forEach(b => b.classList.remove('selected'));
        this.classList.add('selected');
        selectedMode = this.dataset.mode;
    });
});

// Add route form
document.getElementById('routeForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const routeData = {
        name: document.getElementById('routeName').value,
        origin: document.getElementById('fromLocation').value,
        destination: document.getElementById('toLocation').value,
        mode: selectedMode,
        distance: parseFloat(document.getElementById('distance').value),
        duration: parseInt(document.getElementById('duration').value),
        cost: parseFloat(document.getElementById('cost').value)
    };

    try {
        const response = await fetch(`${API_URL}/routes`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(routeData)
        });

        if (!response.ok) throw new Error('Failed to create route');

        this.reset();
        document.querySelectorAll('.mode-btn').forEach(b => b.classList.remove('selected'));
        document.querySelector('.mode-btn[data-mode="car"]').classList.add('selected');
        selectedMode = 'car';
        
        await loadData();
        showNotification('Route added successfully');
    } catch (error) {
        console.error('Error creating route:', error);
        showNotification('Error adding route', 'error');
    }
});

// Render routes
function renderRoutes() {
    const allRoutesList = document.getElementById('routesList');
    const favoritesList = document.getElementById('favoritesList');
    
    if (routes.length === 0) {
        allRoutesList.innerHTML = '<div class="empty-state"><div class="empty-state-icon">📍</div><p>No routes added yet.<br>Create your first route to get started.</p></div>';
        favoritesList.innerHTML = '<div class="empty-state"><div class="empty-state-icon">⭐</div><p>No favorite routes yet.<br>Mark routes as favorites for quick access.</p></div>';
        return;
    }

    allRoutesList.innerHTML = routes.map(route => `
        <div class="route-item">
            <div class="route-header">
                <div class="route-name">${route.name}</div>
                <button class="favorite-btn" onclick="toggleFavorite(${route.id})">
                    ${route.favorite ? '⭐' : '☆'}
                </button>
            </div>
            <div class="route-path">${route.origin} → ${route.destination}</div>
            <div class="route-details">
                <div class="detail-item">
                    <span class="detail-label">Mode</span>
                    <span class="detail-value">${modeEmojis[route.mode]} ${modeNames[route.mode]}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Distance</span>
                    <span class="detail-value">${route.distance} km</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Duration</span>
                    <span class="detail-value">${route.duration} min</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Cost</span>
                    <span class="detail-value">₹${route.cost.toFixed(2)}</span>
                </div>
            </div>
            <div class="route-actions">
                <button class="btn btn-small btn-danger" onclick="deleteRoute(${route.id})">Delete Route</button>
            </div>
        </div>
    `).join('');

    const favorites = routes.filter(r => r.favorite);
    if (favorites.length === 0) {
        favoritesList.innerHTML = '<div class="empty-state"><div class="empty-state-icon">⭐</div><p>No favorite routes yet.<br>Mark routes as favorites for quick access.</p></div>';
    } else {
        favoritesList.innerHTML = favorites.map(route => `
            <div class="route-item">
                <div class="route-header">
                    <div class="route-name">${route.name}</div>
                </div>
                <div class="route-path">${route.origin} → ${route.destination}</div>
                <div class="route-details">
                    <div class="detail-item">
                        <span class="detail-label">Mode</span>
                        <span class="detail-value">${modeEmojis[route.mode]} ${modeNames[route.mode]}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Distance</span>
                        <span class="detail-value">${route.distance} km</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Duration</span>
                        <span class="detail-value">${route.duration} min</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Cost</span>
                        <span class="detail-value">₹${route.cost.toFixed(2)}</span>
                    </div>
                </div>
            </div>
        `).join('');
    }
}

// Toggle favorite
async function toggleFavorite(id) {
    try {
        const response = await fetch(`${API_URL}/routes/${id}/favorite`, {
            method: 'PATCH'
        });

        if (!response.ok) throw new Error('Failed to toggle favorite');

        await loadData();
        const route = routes.find(r => r.id === id);
        showNotification(route.favorite ? 'Added to favorites' : 'Removed from favorites');
    } catch (error) {
        console.error('Error toggling favorite:', error);
        showNotification('Error updating favorite', 'error');
    }
}

// Delete route
async function deleteRoute(id) {
    if (!confirm('Are you sure you want to delete this route?')) return;

    try {
        const response = await fetch(`${API_URL}/routes/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('Failed to delete route');

        await loadData();
        showNotification('Route deleted successfully');
    } catch (error) {
        console.error('Error deleting route:', error);
        showNotification('Error deleting route', 'error');
    }
}

// Tab switching
document.querySelectorAll('.tab').forEach(tab => {
    tab.addEventListener('click', function() {
        const targetTab = this.dataset.tab;
        
        document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
        
        this.classList.add('active');
        document.getElementById(targetTab).classList.add('active');
    });
});

// Update schedule dropdown
function updateScheduleDropdown() {
    const select = document.getElementById('scheduleRoute');
    select.innerHTML = '<option value="">Select Route</option>' + 
        routes.map(r => `<option value="${r.id}">${r.name}</option>`).join('');
}

// Add schedule form
document.getElementById('scheduleForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const scheduleData = {
        routeId: parseInt(document.getElementById('scheduleRoute').value),
        time: document.getElementById('scheduleTime').value,
        days: document.getElementById('scheduleDays').value
    };

    try {
        const response = await fetch(`${API_URL}/schedules`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(scheduleData)
        });

        if (!response.ok) throw new Error('Failed to create schedule');

        this.reset();
        await fetchSchedules();
        showNotification('Schedule added successfully');
    } catch (error) {
        console.error('Error creating schedule:', error);
        showNotification('Error adding schedule', 'error');
    }
});

// Render schedules
function renderSchedules() {
    const scheduleList = document.getElementById('scheduleList');
    
    if (schedules.length === 0) {
        scheduleList.innerHTML = '<div class="empty-state"><div class="empty-state-icon">📅</div><p>No scheduled routes yet.<br>Create a schedule for recurring commutes.</p></div>';
        return;
    }

    const daysDisplay = {
        weekdays: 'Monday - Friday',
        daily: 'Every Day',
        weekends: 'Saturday - Sunday'
    };

    scheduleList.innerHTML = schedules.map(schedule => `
        <div class="schedule-item">
            <div class="schedule-time">${schedule.time}</div>
            <div style="font-weight: 600; color: #2c3e50; margin-bottom: 5px;">${schedule.routeName || 'Unknown Route'}</div>
            <div style="color: #7f8c8d; font-size: 0.9em; margin-bottom: 15px;">
                ${daysDisplay[schedule.days]}
            </div>
            <button class="btn btn-small btn-danger" onclick="deleteSchedule(${schedule.id})">Remove Schedule</button>
        </div>
    `).join('');
}

// Delete schedule
async function deleteSchedule(id) {
    try {
        const response = await fetch(`${API_URL}/schedules/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('Failed to delete schedule');

        await fetchSchedules();
        showNotification('Schedule removed');
    } catch (error) {
        console.error('Error deleting schedule:', error);
        showNotification('Error removing schedule', 'error');
    }
}

// Show notification
function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Initialize application
loadData();
// Global variables
let routes = [];
let schedules = [];
let selectedMode = 'car';

// Carbon emission factors (kg CO2 per km)
const emissionFactors = {
    car: 0.171,
    bus: 0.089,
    train: 0.041,
    bike: 0,
    walk: 0
};

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

// Load data from storage
async function loadData() {
    try {
        const routesData = await window.storage.get('commute-routes');
        if (routesData) {
            routes = JSON.parse(routesData.value);
        }
        
        const schedulesData = await window.storage.get('commute-schedules');
        if (schedulesData) {
            schedules = JSON.parse(schedulesData.value);
        }
    } catch (error) {
        console.log('No existing data found, starting fresh');
        routes = [];
        schedules = [];
    }
    
    renderRoutes();
    updateStatistics();
    updateScheduleDropdown();
    renderSchedules();
}

// Save data to storage
async function saveData() {
    try {
        await window.storage.set('commute-routes', JSON.stringify(routes));
        await window.storage.set('commute-schedules', JSON.stringify(schedules));
    } catch (error) {
        console.error('Error saving data:', error);
        showNotification('Error saving data');
    }
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
    
    const route = {
        id: Date.now(),
        name: document.getElementById('routeName').value,
        from: document.getElementById('fromLocation').value,
        to: document.getElementById('toLocation').value,
        mode: selectedMode,
        distance: parseFloat(document.getElementById('distance').value),
        duration: parseInt(document.getElementById('duration').value),
        cost: parseFloat(document.getElementById('cost').value),
        favorite: false,
        createdAt: new Date().toISOString()
    };

    routes.push(route);
    await saveData();
    
    this.reset();
    document.querySelectorAll('.mode-btn').forEach(b => b.classList.remove('selected'));
    document.querySelector('.mode-btn[data-mode="car"]').classList.add('selected');
    selectedMode = 'car';
    
    renderRoutes();
    updateStatistics();
    updateScheduleDropdown();
    showNotification('Route added successfully');
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
            <div class="route-path">${route.from} → ${route.to}</div>
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
                <div class="route-path">${route.from} → ${route.to}</div>
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
    const route = routes.find(r => r.id === id);
    if (route) {
        route.favorite = !route.favorite;
        await saveData();
        renderRoutes();
        showNotification(route.favorite ? 'Added to favorites' : 'Removed from favorites');
    }
}

// Delete route
async function deleteRoute(id) {
    if (confirm('Are you sure you want to delete this route?')) {
        routes = routes.filter(r => r.id !== id);
        schedules = schedules.filter(s => s.routeId !== id);
        await saveData();
        renderRoutes();
        updateStatistics();
        updateScheduleDropdown();
        renderSchedules();
        showNotification('Route deleted successfully');
    }
}

// Update statistics
function updateStatistics() {
    document.getElementById('totalRoutes').textContent = routes.length;
    
    const totalDistance = routes.reduce((sum, r) => sum + r.distance, 0);
    document.getElementById('totalDistance').textContent = totalDistance.toFixed(1);
    
    const totalCost = routes.reduce((sum, r) => sum + r.cost, 0);
    document.getElementById('totalCost').textContent = '₹' + totalCost.toFixed(0);
    
    const totalTime = routes.reduce((sum, r) => sum + r.duration, 0);
    document.getElementById('totalTime').textContent = (totalTime / 60).toFixed(1) + 'h';
    
    // Calculate carbon footprint (monthly estimate assuming 22 working days)
    const monthlyCarbon = routes.reduce((sum, r) => {
        return sum + (r.distance * emissionFactors[r.mode] * 22 * 2);
    }, 0);
    document.getElementById('carbonFootprint').textContent = monthlyCarbon.toFixed(1) + ' kg CO₂';
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

// Add schedule
document.getElementById('scheduleForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const routeId = parseInt(document.getElementById('scheduleRoute').value);
    const route = routes.find(r => r.id === routeId);
    
    if (!route) return;
    
    const schedule = {
        id: Date.now(),
        routeId: routeId,
        routeName: route.name,
        time: document.getElementById('scheduleTime').value,
        days: document.getElementById('scheduleDays').value
    };

    schedules.push(schedule);
    await saveData();
    
    this.reset();
    renderSchedules();
    showNotification('Schedule added successfully');
});

// Render schedules
function renderSchedules() {
    const scheduleList = document.getElementById('scheduleList');
    
    if (schedules.length === 0) {
        scheduleList.innerHTML = '<div class="empty-state"><div class="empty-state-icon">📅</div><p>No scheduled routes yet.<br>Create a schedule for recurring commutes.</p></div>';
        return;
    }

    schedules.sort((a, b) => a.time.localeCompare(b.time));

    const daysDisplay = {
        weekdays: 'Monday - Friday',
        daily: 'Every Day',
        weekends: 'Saturday - Sunday'
    };

    scheduleList.innerHTML = schedules.map(schedule => `
        <div class="schedule-item">
            <div class="schedule-time">${schedule.time}</div>
            <div style="font-weight: 600; color: #2c3e50; margin-bottom: 5px;">${schedule.routeName}</div>
            <div style="color: #7f8c8d; font-size: 0.9em; margin-bottom: 15px;">
                ${daysDisplay[schedule.days]}
            </div>
            <button class="btn btn-small btn-danger" onclick="deleteSchedule(${schedule.id})">Remove Schedule</button>
        </div>
    `).join('');
}

// Delete schedule
async function deleteSchedule(id) {
    schedules = schedules.filter(s => s.id !== id);
    await saveData();
    renderSchedules();
    showNotification('Schedule removed');
}

// Show notification
function showNotification(message) {
    const notification = document.createElement('div');
    notification.className = 'notification';
    notification.textContent = message;
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Initialize application
loadData();
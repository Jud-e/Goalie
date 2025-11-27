AOS.init({
    duration: 800,
    easing: 'ease-in-out',
    once: true
});

feather.replace();

// Initialize Vanta.js globe effect
VANTA.GLOBE({
    el: "#vanta-globe",
    mouseControls: true,
    touchControls: true,
    gyroControls: false,
    minHeight: 200.00,
    minWidth: 200.00,
    scale: 1.00,
    scaleMobile: 1.00,
    color: 0x3a82ff,
    backgroundColor: 0x1665d8,
    size: 0.8
});

const billingSection = document.getElementById("billingSection")
const premiumToggle = document.getElementById("premiumToggle")

premiumToggle.addEventListener("change", (event) => {
    if(event.target.checked){
        billingSection.style.display = "block"
    }else {
        billingSection.style.display = "none"
    }

})

function clearFilters() {
    document.getElementById('searchInput').value = '';
    document.getElementById('statusFilter').value = '';
    document.getElementById('locationFilter').value = '';
    document.getElementById('startDateFilter').value = '';
    document.getElementById('endDateFilter').value = '';
    window.location.href = '/tournaments';
}

// Auto-submit on filter change
document.addEventListener('DOMContentLoaded', function() {
    const filterSelects = document.querySelectorAll('.filter-select, .filter-date');
    filterSelects.forEach(select => {
        select.addEventListener('change', function() {
            document.getElementById('searchFilterForm').submit();
        });
    });
});

// Re-initialize feather icons after page load
document.addEventListener('DOMContentLoaded', function() {
    if (typeof feather !== 'undefined') {
        feather.replace();
    }
});

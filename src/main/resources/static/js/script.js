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


const  regularBtn = document.getElementById("regular")

const  premiumBtn = document.getElementById("premium")

const  regularForm = document.querySelector(".regular-form")

const  premiumForm = document.querySelector(".premium-form")
const  profileSection = document.querySelector(".profile_section")


regularBtn.addEventListener('click', ()=>{
    regularBtn.style.backgroundColor = "#21264D";
    premiumBtn.style.backgroundColor = "rgba(255,255,255,0.2)"

    regularForm.style.left = "50%"
    premiumForm.style.left = "-50%"

    regularForm.style.opacity = 1;
    premiumForm.style.opacity = 0;

    profileSection.style.height = "930px"

})

premiumBtn.addEventListener('click', ()=>{
    premiumBtn.style.backgroundColor = "#21264D";
    regularBtn.style.backgroundColor = "rgba(255,255,255,0.2)"

    regularForm.style.left = "150%"
    premiumForm.style.left = "50%"

    regularForm.style.opacity = 0;
    premiumForm.style.opacity = 1;

    profileSection.style.height = "1350px"

})
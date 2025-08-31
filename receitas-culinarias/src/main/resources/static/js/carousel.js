let currentSlide = 0;
const slides = document.querySelectorAll(".slide");
const dotsContainer = document.querySelector(".dots");

// Criar bolinhas
slides.forEach((_, index) => {
    const dot = document.createElement("span");
    dot.addEventListener("click", () => showSlide(index));
    dotsContainer.appendChild(dot);
});

const dots = dotsContainer.querySelectorAll("span");

function showSlide(index) {
    slides[currentSlide].classList.remove("active");
    dots[currentSlide].classList.remove("active");

    currentSlide = index;

    slides[currentSlide].classList.add("active");
    dots[currentSlide].classList.add("active");
}

// Inicia primeira
showSlide(0);

// Auto play
setInterval(() => {
    let next = (currentSlide + 1) % slides.length;
    showSlide(next);
}, 5000);

const tooltip = document.getElementById("item-tooltip");
const tooltipDetails = tooltip.querySelector(".item-tooltip__detail");

const inventories = document.getElementsByClassName("inventory");

const slots = document.getElementsByClassName("slot");
const slotCycles = document.getElementsByClassName("slot--cycle");

const updateTooltipPosition = function(mouseX, mouseY) {
    tooltip.style.left = (mouseX + 16) + "px";
    tooltip.style.top = (mouseY) + "px";
}

let zoom = window.localStorage.getItem("zoom");
if (!zoom) {
    window.localStorage.setItem("zoom", "1");
    zoom = "1";
} else if (zoom === "1.5") {
    const body = document.body;
    body.classList.add("inventory__zoom1_5");
} else if (zoom === "2") {
    const body = document.body;
    body.classList.add("inventory__zoom2");
}

const zoomOut = function() {
    const body = document.body;

    if (body.classList.contains("inventory__zoom2")) {
        body.classList.remove("inventory__zoom2");
        body.classList.add("inventory__zoom1_5");
        window.localStorage.setItem("zoom", "1.5");
    } else {
        body.classList.remove("inventory__zoom1_5");
        window.localStorage.setItem("zoom", "1");
    }
}

const zoomIn = function() {
    const body = document.body;

    if (body.classList.contains("inventory__zoom1_5")) {
        body.classList.remove("inventory__zoom1_5");
        body.classList.add("inventory__zoom2");
        window.localStorage.setItem("zoom", "2");
    } else {
        body.classList.add("inventory__zoom1_5");
        window.localStorage.setItem("zoom", "1.5");
    }
}

Array.from(inventories).forEach(function(el) {
    const zoomOutSpan = document.createElement("span");
    zoomOutSpan.classList.add("inventory__zoom-out");
    zoomOutSpan.addEventListener('click', zoomOut);

    const zoomInSpan = document.createElement("span");
    zoomInSpan.classList.add("inventory__zoom-in");
    zoomInSpan.addEventListener('click', zoomIn);

    el.appendChild(zoomOutSpan);
    el.appendChild(zoomInSpan);
});


const mouseEnter = function(el) {
    const target = el.target;
    let detail;
    const slotCycle = target.querySelector(".slot__cycle.active");
    if (slotCycle) {
        detail = slotCycle.querySelector(".detail");
    } else {
        detail = target.querySelector(".detail");
    }
    if (detail) {
        target.classList.add("active");
        tooltipDetails.innerHTML = detail.innerHTML;
        tooltip.classList.add("active");
        updateTooltipPosition(el.pageX, el.pageY);
    }
};

const mouseLeave = function(el) {
    el.target.classList.remove("active");
    tooltip.classList.remove("active");
};

const mouseMove = function(el) {
    if (tooltip.classList.contains("active")) {
        updateTooltipPosition(el.pageX, el.pageY);
    }
};

Array.from(slots).forEach(function(el) {
    el.addEventListener('mouseenter', mouseEnter);
    el.addEventListener('mouseleave', mouseLeave);
    el.addEventListener('mousemove', mouseMove);
});

let currentCycle = 1;
const toggleSlots = function(maxCycles) {
    Array.from(slotCycles).forEach(function(el) {
        if (currentCycle % el.getAttribute('data-cycle') === 0) {
            toggleSlotCycle(el);
        }
    });

    currentCycle++;
    if (currentCycle > maxCycles) {
        currentCycle = 1;
    }
};

const toggleSlotCycle = function(el) {
    const activeCycle = el.querySelector(".slot__cycle.active");
    let nextCycle;
    if (activeCycle) {
        activeCycle.classList.remove("active");

        nextCycle = activeCycle.nextElementSibling;
    }

    if (!nextCycle) {
        nextCycle = el.firstElementChild;
    }
    nextCycle.classList.add("active");

    if (el.classList.contains("active")) {
        const detail = nextCycle.querySelector(".detail");
        tooltipDetails.innerHTML = detail.innerHTML;
    }
}

if (slotCycles.length > 0) {
    let maxCycles = 1;
    Array.from(slotCycles).forEach(function(el) {
        const dataCycle = el.getAttribute('data-cycle');
        if (dataCycle > maxCycles) {
            maxCycles = dataCycle;
        }
    });

    setInterval(toggleSlots, 1000, maxCycles);
}
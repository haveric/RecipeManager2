var tooltip = document.getElementById("item-tooltip");
var tooltipDetails = tooltip.querySelector(".item-tooltip__detail");

var inventories = document.getElementsByClassName("inventory");

var slots = document.getElementsByClassName("slot");
var slotCycles = document.getElementsByClassName("slot--cycle");

var updateTooltipPosition = function(mouseX, mouseY) {
    tooltip.style.left = (mouseX + 16) + "px";
    tooltip.style.top = (mouseY) + "px";
}

var zoom = window.localStorage.getItem("zoom");
if (!zoom) {
    window.localStorage.setItem("zoom", "1");
    zoom = "1";
} else if (zoom == "1.5") {
    var body = document.body;
    body.classList.add("inventory__zoom1_5");
} else if (zoom == "2") {
    var body = document.body;
    body.classList.add("inventory__zoom2");
}

var zoomOut = function() {
    var body = document.body;

    if (body.classList.contains("inventory__zoom2")) {
        body.classList.remove("inventory__zoom2");
        body.classList.add("inventory__zoom1_5");
        window.localStorage.setItem("zoom", "1.5");
    } else {
        body.classList.remove("inventory__zoom1_5");
        window.localStorage.setItem("zoom", "1");
    }
}

var zoomIn = function() {
    var body = document.body;

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
    var zoomOutSpan = document.createElement("span");
    zoomOutSpan.classList.add("inventory__zoom-out");
    zoomOutSpan.addEventListener('click', zoomOut);

    var zoomInSpan = document.createElement("span");
    zoomInSpan.classList.add("inventory__zoom-in");
    zoomInSpan.addEventListener('click', zoomIn);

    el.appendChild(zoomOutSpan);
    el.appendChild(zoomInSpan);
});


var mouseEnter = function(el) {
    var target = el.target;
    var detail;
    var slotCycle = target.querySelector(".slot__cycle.active");
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

var mouseLeave = function(el) {
    el.target.classList.remove("active");
    tooltip.classList.remove("active");
};

var mouseMove = function(el) {
    if (tooltip.classList.contains("active")) {
        updateTooltipPosition(el.pageX, el.pageY);
    }
};

Array.from(slots).forEach(function(el) {
    el.addEventListener('mouseenter', mouseEnter);
    el.addEventListener('mouseleave', mouseLeave);
    el.addEventListener('mousemove', mouseMove);
});

var currentCycle = 1;
var toggleSlots = function(maxCycles) {
    Array.from(slotCycles).forEach(function(el) {
        if (currentCycle % el.getAttribute('data-cycle') == 0) {
            toggleSlotCycle(el);
        }
    });

    currentCycle++;
    if (currentCycle > maxCycles) {
        currentCycle = 1;
    }
};

var toggleSlotCycle = function(el) {
    var activeCycle = el.querySelector(".slot__cycle.active");
    activeCycle.classList.remove("active");

    var nextCycle = activeCycle.nextElementSibling;

    if (!nextCycle) {
        nextCycle = el.firstElementChild;
    }
    nextCycle.classList.add("active");

    if (el.classList.contains("active")) {
        var detail = nextCycle.querySelector(".detail");
        tooltipDetails.innerHTML = detail.innerHTML;
    }
}

if (slotCycles.length > 0) {
    var maxCycles = 1;
    Array.from(slotCycles).forEach(function(el) {
        var dataCycle = el.getAttribute('data-cycle');
        if (dataCycle > maxCycles) {
            maxCycles = dataCycle;
        }
    });

    setInterval(toggleSlots, 1000, maxCycles);
}
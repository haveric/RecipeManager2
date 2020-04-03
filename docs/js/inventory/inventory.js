var tooltip = document.getElementById("item-tooltip");
var tooltipDetails = tooltip.querySelector(".item-tooltip__detail");

var slots = document.getElementsByClassName("slot");

var updateTooltipPosition = function(mouseX, mouseY) {
    tooltip.style.left = (mouseX + 16) + "px";
    tooltip.style.top = (mouseY) + "px";
}


var mouseEnter = function(el) {
    var target = el.target;
    var detail = target.querySelector(".detail");
    if (detail) {
        tooltipDetails.innerHTML = detail.innerHTML;
        tooltip.classList.add("active");
        updateTooltipPosition(el.pageX, el.pageY);
    }
};

var mouseLeave = function(el) {
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
var getSiblings = function(el) {
    return Array.prototype.filter.call(el.parentNode.children, function(child){
        return child !== el;
    });
}

function getIndex(el) {
    if (!el) return -1;
    var i = 0;
    do {
        i++;
    } while (el = el.previousElementSibling);
    return i - 1;
}

var switchTab = function() {
    if (!this.classList.contains("active")) {
        for (var sibling of getSiblings(this)) {
            sibling.classList.remove("active");
        }
        this.classList.add("active");

        var index = getIndex(this);

        var tabsContent = this.parentNode.nextElementSibling;
        var currentActiveContent = tabsContent.querySelector(".tab__content.active");

        currentActiveContent.classList.remove("active");
        var newActiveContent = tabsContent.children[index];
        newActiveContent.classList.add("active");
    }
}

var tabNavs = document.getElementsByClassName("tab__title");

Array.from(tabNavs).forEach(function(el) {
    el.addEventListener('click', switchTab);
});
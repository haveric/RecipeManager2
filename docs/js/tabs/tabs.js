const getSiblings = function(el) {
    return Array.prototype.filter.call(el.parentNode.children, function(child){
        return child !== el;
    });
}

function getIndex(el) {
    if (!el) return -1;
    let i = 0;
    do {
        i++;
    } while (el = el.previousElementSibling);
    return i - 1;
}

const switchTab = function() {
    if (!this.classList.contains("active")) {
        for (let sibling of getSiblings(this)) {
            sibling.classList.remove("active");
        }
        this.classList.add("active");

        const index = getIndex(this);

        const tabsContent = this.parentNode.nextElementSibling;
        const currentActiveContent = tabsContent.querySelector(".tab__content.active");

        currentActiveContent.classList.remove("active");
        const newActiveContent = tabsContent.children[index];
        newActiveContent.classList.add("active");
    }
}

const tabNavs = document.getElementsByClassName("tab__title");

Array.from(tabNavs).forEach(function(el) {
    el.addEventListener('click', switchTab);
});
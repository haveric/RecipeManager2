$(function() {
    var $itemToolTip = $("#itemTooltip");
    var $itemToolTipInn = $itemToolTip.find(".inn");
    
    $('.slot').on({
        mouseenter: function(e) {
            var $this = $(this);
            var $parent = $this.parents(".inventory");
            var $detail = $this.find(".detail").html();
            if ($detail && $detail != "") {
                $itemToolTipInn.html($detail);
                $itemToolTip.addClass("active");
                updateTooltipPosition(e.pageX, e.pageY, $parent);
            }
            
        },
        mouseleave: function() {
            $itemToolTip.removeClass("active");
            $itemToolTip.show().hide();
        },
        mousemove: function(e) {
            var $parent = $(this).parents(".inventory");
            if (e && $itemToolTip.hasClass("active")) {
                updateTooltipPosition(e.pageX, e.pageY, $parent);
            }
        }
    });

    function updateTooltipPosition(mouseX, mouseY, $inventory) { 
        var inventoryWidth = $inventory.width();

        var toolTipLeft = 0;
        var cellLeft = mouseX - $inventory.offset().left;
        if (cellLeft < inventoryWidth / 2) {
            toolTipLeft = mouseX + 16;
        } else {
            toolTipLeft = mouseX - $itemToolTip.width() - 16;
        }

        $itemToolTip.show().css({
            left: toolTipLeft,
            top: mouseY - $inventory.offset().top - 30
        });
    }
});


$(function() {
    String.prototype.startsWith = function(str) 
    {return (this.match("^"+str)==str)}

    var $itemToolTip = $("#itemTooltip");
    var $itemToolTipInn = $itemToolTip.find(".inn");
    
    $('.inventory .slot').on({
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
        },
        click: function(e) {
            var $this = $(this);
            $(".slot").removeClass("selected");
            $this.addClass("selected");
            
            var name = $this.find(".title").text();
            var customName = $this.find(".customTitle").text();
            
            $("#defaultTitle").text(name);
            $("#customTitle").text(customName);
            
        }
    });
    
    $(".inventory .slot").droppable({
        tolerance: "pointer",
        hoverClass: 'dropHover',
        drop: function( event, ui ) {
            var $this = $(this);
            var $draggable = ui.draggable;
            var img = $draggable.find("img").attr('src');
            var $detail = $draggable.find(".detail").html();
            
            $this.find(".detail").html($detail);
            $this.find("img").attr('src', img);
        }
    });
    
    
    $.getJSON("http://api.wurstmineberg.de/minecraft/items/all.json", function(items) {
        var searchHtml = "";
        // console.log("Items: " + items.count, items.minecraft);
        $.each(items.minecraft, function(item) {
            searchHtml += '<div class="item">';
            var image = this.image;

            if (image && !image.startsWith("http")) {
                image = "http://assets.wurstmineberg.de/img/grid/" + image;
            }
            searchHtml += '<img src="' + image + '" />';
            searchHtml += '<div class="detail">';
            searchHtml += '<span class="line title">' + this.name + '</span>';
            searchHtml += '</div></div>';
        });
        
        $(".search").html(searchHtml);
        
        $(".search .item").draggable({
            helper: "clone",
            appendTo: ".container",
            zIndex: 100,
            revert: true,
            revertDuration: 0
        });
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
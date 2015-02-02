$(function() {
    String.prototype.startsWith = function(str) 
    {return (this.match("^"+str)==str)}

    var self = this;
    var $itemToolTip = $("#itemTooltip");
    var $itemToolTipInn = $itemToolTip.find(".inn");
    var searchTimeout;
    
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
            $this.addClass("tooltip");
        },
        mouseleave: function() {
            var $this = $(this);
            
            $itemToolTip.removeClass("active");
            $itemToolTip.show().hide();
            $this.removeClass("tooltip");
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
            var customName = $this.find(".customTitle .original").text();
            
            $("#defaultTitle").text(name);
            $("#customTitle").val(customName);
        }
    });
    $(".inventory .slot").draggable({
        helper: "clone",
        appendTo: ".container",
        zIndex: 100,
        revert: true,
        revertDuration: 0
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
            
            $this.draggable({
                helper: "clone",
                appendTo: ".container",
                zIndex: 100,
                revert: true,
                revertDuration: 0
            });
            
            updateOutput();
        }
    });
    
    $("#customTitle").on("input", function() {
        var $slotToUpdate = $(".slot.selected");
        
        var customTitle = $(this).val();
        var originalTitle = customTitle;
        
        customTitle = parseColors(customTitle);
        
        var $slotCustomTitle = $slotToUpdate.find(".customTitle");
        if (customTitle == "") {
            $slotCustomTitle.remove();
        } else {
            if ($slotCustomTitle.length <= 0) {
                $slotToUpdate.find(".title").before("<span class='line customTitle'></span>");
                
                $slotCustomTitle = $slotToUpdate.find(".customTitle");
            }
            
            $slotCustomTitle.html(customTitle);
        }
        
        var $originalTitle = $slotCustomTitle.find(".original");
        if ($originalTitle.length <= 0) {
            $slotCustomTitle.append("<span class='original'></span>");
            
            $originalTitle = $slotCustomTitle.find(".original");
        }
        
        $originalTitle.text(originalTitle);
        
        updateActiveTooltip();
    });
    
    $("#searchInput").on("input", function() {
        var searchTerm = $(this).val().toLowerCase();
        
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(function () {
            $("#search-items .item").each(function() {
                var itemText = $(this).text().toLowerCase();

                if (itemText.indexOf(searchTerm) > -1) {
                    $(this).removeClass("hidden");
                } else {
                    $(this).addClass("hidden");
                }
            });
        }, 150);
    });
    
    $.getJSON("http://api.wurstmineberg.de/minecraft/items/all.json", function(items) {
        var searchHtml = "";

        $.each(items.minecraft, function(item) {
            searchHtml += '<div class="item">';
            var image = this.image;

            // Default to air for any missing images
            if (image === undefined) {
                image = "air.png";
                console.error("Missing image for: ", item);
            }
            
            if (image && !image.startsWith("http")) {
                image = "http://assets.wurstmineberg.de/img/grid/" + image;
            }
            
            
            searchHtml += '<img src="' + image + '" />';
            searchHtml += '<div class="detail">';
            searchHtml += '<span class="line title">' + this.name + '</span>';
            searchHtml += '</div></div>';
        });
        
        $("#search-items").html(searchHtml);
        
        $("#search-items .item").draggable({
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
    
    function updateActiveTooltip() {
        if ($itemToolTip.hasClass("active")) {
            var $detail = $(".slot.tooltip").find(".detail").html();
            $itemToolTipInn.html($detail);
        }
    }
    
    function parseColors(string) {
        var parsedString = "";
        var split = string.split("&");
        
        var color = "white";
        var bold = "";
        var underline = "";
        var obfuscated = "";
        var strikethrough = "";
        var italic = "";
        
        
        for (var i = 0; i < split.length; i++) {
            var splitToCheck = split[i];
            
            var colorCode = getColorCode(splitToCheck);
            if (i == 0) {
                parsedString += splitToCheck;
            } else {
                var actualText = "";
                if (colorCode == "") {
                    actualText = "&" + splitToCheck;
                } else {
                    actualText = splitToCheck.substring(1);
                    if (colorCode == "reset") {
                        color = "white";
                        bold = "";
                        underline = "";
                        obfuscated = "";
                        strikethrough = "";
                        italic = " noitalic";
                    } else if (colorCode == "bold") {
                        bold = " bold";
                    } else if (colorCode == "underline") {
                        underline = " underline";
                    } else if (colorCode == "obfuscated") {
                        obfuscated = " obfuscated";
                    } else if (colorCode == "strikethrough") {
                        strikethrough = " strikethrough";
                    } else if (colorCode == "italic") {
                        italic = " italic";
                    } else {
                        color = colorCode;
                        bold = "";
                        underline = "";
                        obfuscated = "";
                        strikethrough = "";
                        italic = " noitalic";
                    }
                }
                
                
                parsedString += "<span class='color-" + color + bold + underline + obfuscated + italic + "'><a class='" + strikethrough + "'>" + actualText + "</a></span>";
            }
        }
        
        return parsedString;
    }
    
    function getColorCode(string) {
        var code = "";
        if (string.length > 0) {
            var char = string.charAt(0);
            
            switch(char) {
                case '0':
                    code = "black";
                    break;
                case '1':
                    code = "darkBlue";
                    break;
                case '2':
                    code = "darkGreen";
                    break;
                case '3':
                    code = "darkAqua";
                    break;
                case '4':
                    code = "darkRed";
                    break;
                case '5':
                    code = "darkPurple";
                    break;
                case '6':
                    code = "gold";
                    break;
                case '7':
                    code = "gray";
                    break;
                case '8':
                    code = "darkGray";
                    break;
                case '9':
                    code = "blue";
                    break;
                case 'a':
                    code = "green";
                    break;
                case 'b':
                    code = "aqua";
                    break;
                case 'c':
                    code = "red";
                    break;
                case 'd':
                    code = "lightPurple";
                    break;
                case 'e':
                    code = "yellow";
                    break;
                case 'f':
                    code = "white";
                    break;
                case 'k':
                    code = "obfuscated";
                    break;
                case 'l':
                    code = "bold";
                    break;
                case 'm':
                    code = "strikethrough";
                    break;
                case 'n':
                    code = "underline";
                    break;
                case 'o':
                    code = "italic";
                    break;
                case 'r':
                    code = "reset";
                    break;
                default:
                    code = "";
                    break;
            }
        }
        
        return code;
    }
    
    function updateOutput() {
        var $output = $("#recipeOutput");
        var recipeType;
        var recipe = "";
        
        var $inventory = $(".inventory.active");
        if ($inventory.hasClass("crafting")) {
            recipeType = "craft";
        } else if ($inventory.hasClass("combine")) {
            recipeType = "combine";
        } else if ($inventory.hasClass("furnace")) {
            recipeType = "smelt";
        }
        
        recipe += recipeType + "\n";
        
        $inventory.find(".row").each(function() {
            $(this).find(".slot").each(function(index) {
                var $this = $(this);
                var $title = $this.find(".title");
                if ($title.length <= 0) {
                    if (recipeType == "craft") {
                        recipe += "air";
                    }
                } else {
                    recipe += $title.text();
                }
                    
                if (index < 2) {
                    if (recipeType == "craft") {
                        recipe += " + ";
                    } else if (recipeType = "combine") {
                        recipe += "\n";
                    }
                }
            });
            recipe += "\n";
        });
        
        recipe += "= ";
        
        $output.val(recipe);
    }
});
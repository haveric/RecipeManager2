var spanOf = function(clazz, text) {
    return '<span class="' + clazz + '">' + text + '</span>';
}

var syntaxHighlight = function(el) {
    var html = el.innerHTML;

    html = html.replace(/\/\*(.*\n?)[^\*\/]*\*\//gm, function(match) {
        return spanOf("code__comment", match);
    });

    var lines = html.split("\n");

    for (var i = 0; i < lines.length; i++) {
        var line = lines[i];

        line = line.replace(/(\/\/|#).*/, function(match) {
            return spanOf("code__comment", match);
        });

        line = line.replace(/\[[^\]]*\]/g, function(match) {
            return spanOf("code__optional", match);
        });

        line = line.replace(/&lt;[^;]*&gt;/g, function(match) {
            return spanOf("code__required", match);
        });

        line = line.replace(/^ *=[^"']/, function(match) {
            return spanOf("code__result", match);
        });

        line = line.replace(/@[^ ]*/, function(match) {
            return spanOf("code__flag", match);
        });


        line = line.replace(/^ *\b(craft|combine|smelt|blasting|smoking|fuel|campfire|stonecutting|compost|anvil|grindstone|cartography|brew)\b/i, function(match) {
            return spanOf("code__recipe-tag", match);
        });

        lines[i] = line;
    }
    html = lines.join("\n");
    el.innerHTML = html;
}

var codeBlocks = document.querySelectorAll(".code");
for (var i = 0; i < codeBlocks.length; i++) {
    syntaxHighlight(codeBlocks[i]);
}
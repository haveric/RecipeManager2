var spanOf = function(clazz, text) {
    return '<span class="' + clazz + '">' + text + '</span>';
}

var syntaxHighlight = function(el) {
    var html = el.innerHTML;

    html = html.replace(/\/\*(.*\n?)[^\*\/]*\*\//gm, function(match) {
        return spanOf("code__comment", match);
    });

    html = html.replace(/(\/\/|#).*/gm, function(match) {
        return spanOf("code__comment", match);
    });

    html = html.replace(/\[[^\]]*\]*/gm, function(match) {
        return spanOf("code__optional", match);
    });

    html = html.replace(/\[|\]/gm, function(match) {
        return spanOf("code__optional-char", match);
    });

    html = html.replace(/&lt;[^;]*&gt;/gm, function(match) {
        return spanOf("code__required", match);
    });

    html = html.replace(/&lt;|&gt;/gm, function(match) {
        return spanOf("code__required-char", match);
    });

    html = html.replace(/^ *\b(craft|combine|smelt|blasting|smoking|fuel|campfire|stonecutting|compost|anvil|grindstone|cartography|brew)\b|(&lt;|<)recipe definition(&gt;|>)/gim, function(match) {
        return spanOf("code__recipe-tag", match);
    });

    html = html.replace(/^ *=[^"']/gm, function(match) {
        return spanOf("code__result", match);
    });

    html = html.replace(/@[^ \n]*/gm, function(match) {
        return spanOf("code__flag", match);
    });

    el.innerHTML = html;
}

var codeBlocks = document.querySelectorAll(".code");
for (var i = 0; i < codeBlocks.length; i++) {
    syntaxHighlight(codeBlocks[i]);
}
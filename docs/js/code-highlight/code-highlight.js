const spanOf = function(clazz, text) {
    return '<span class="' + clazz + '">' + text + '</span>';
}

const syntaxHighlight = function(el) {
    const tagName = el.tagName;
    let html = el.innerHTML;

    let startingSpaces = -1;
    // Trim initial spaces to act like pre while keeping indented source code
    html = html.replace(/.+/g, function(match) {
        if (startingSpaces === -1) {
            const matchLength = match.length;
            const trimmed = match.trimStart();
            const trimmedLength = trimmed.length;
            if (trimmedLength > 0) {
                startingSpaces = matchLength - trimmedLength;
            }
        }

        match = match.substring(startingSpaces);

        return match;
    });

    html = html.replace(/\/\*(.*\n?)[^*\/]*\*\//gm, function(match) {
        let linesHTML = "";
        match.split('\n').forEach((line) => {
            linesHTML += spanOf("code__comment", line) + "\n";
        });

        return linesHTML;
    });

    html = html.replace(/(\/\/|#).*/gm, function(match) {
        return spanOf("code__comment", match);
    });

    html = html.replace(/\[[^\]]*]*/gm, function(match) {
        return spanOf("code__optional", match);
    });

    html = html.replace(/[\[\]]/gm, function(match) {
        return spanOf("code__optional-char", match);
    });

    html = html.replace(/&lt;[^;]*&gt;/gm, function(match) {
        return spanOf("code__required", match);
    });

    html = html.replace(/&lt;|&gt;/gm, function(match) {
        return spanOf("code__required-char", match);
    });

    html = html.replace(/^ *\b(craft|combine|smelt|blasting|smoking|fuel|campfire|stonecutting|compost|anvil|grindstone|cartography|brew|smithing)\b|(&lt;|<)recipe definition(&gt;|>)/gim, function(match) {
        return spanOf("code__recipe-tag", match);
    });

    html = html.replace(/^ *=[^"']/gm, function(match) {
        return spanOf("code__result", match);
    });

    html = html.replace(/@[^ \n]*/gm, function(match) {
        return spanOf("code__flag", match);
    });

    if (tagName !== 'PRE') {
        // Wrap each line so we can format each source line on a new line
        html = html.replace(/.+[\n]*/g, function (match) {
            return spanOf("code__line", match);
        });
    }

    el.innerHTML = html;
}

const codeBlocks = document.querySelectorAll(".code");
for (let i = 0; i < codeBlocks.length; i++) {
    syntaxHighlight(codeBlocks[i]);
}
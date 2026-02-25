export function encode(input) {
    input = input.toLowerCase()
    let output = ""
    for (let i = 0; i < input.length; i++) {
        if (isAllowedChar(input[i])) {
            output += input[i]
        } else if (i > 0 && i < input.length - 1 && input[i - 1] == ' ' && input[i + 1] == ' ') {
            output = output.slice(0, -1)
        }
    }
    return output.replaceAll(" ", "-")
}

function isAllowedChar(c) {
    return /^[\p{L}\p{N}\-._~ ]$/u.test(c);
}
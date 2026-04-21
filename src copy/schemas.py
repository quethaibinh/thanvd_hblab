import html


def clean_text(value):
    if not value:
        return value
    return html.unescape(value)

<#macro masterTemplate>
    <!doctype html>
    <html>
    <head>
        <meta charset="utf-8">
        <title>${title}</title>
        <link rel="stylesheet" type="text/css" href="/css/pure-min.css">
        <link rel="stylesheet" type="text/css" href="/css/app.css">
        <script type="text/javascript" src="/js/jquery.min.js"></script>
    </head>
    <body>
        <#nested />
    </body>
    </html>

</#macro>

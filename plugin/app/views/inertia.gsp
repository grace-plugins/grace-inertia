<!doctype html>
<html>
<head>
    <title>Inertia Html Template</title>
    <g:if env="production">
        <script type="module" src="/static/dist/${inertiaManifest['app/javascript/main.js']['file']}"></script>
        <g:each in="${inertiaManifest['app/javascript/main.js']['css']}" var="inertiaCss">
            <link rel="stylesheet" href="/static/dist/${inertiaCss}">
        </g:each>
    </g:if>
    <g:else>
        <script type="module" src="http://localhost:3000/@vite/client"></script>
        <script type="module" src="http://localhost:3000/app/javascript/main.js"></script>
    </g:else>
</head>
<body>
    <inertia:app />
</body>
</html>

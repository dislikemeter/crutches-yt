[#macro layout]
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<meta name="_csrf" content="${_csrf.token}">
<meta name="_csrf_header" content="${_csrf.headerName}">
<link rel="icon" type="image/png" href="/static/img/favicon16.png" sizes="16x16">
<link rel="icon" type="image/png" href="/static/img/favicon32.png" sizes="32x32">
<link rel="stylesheet" type="text/css" href="/static/css/icons.min.css">
<link rel="stylesheet" type="text/css" href="/static/css/commons.min.css">
<link rel="stylesheet" type="text/css" href="/static/css/theme.min.css">
<title>${.main.title}</title>
</head>
<body>
[#nested/]
</body>
</html>
[/#macro]
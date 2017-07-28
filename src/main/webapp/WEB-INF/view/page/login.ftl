[#import "../layout/page.ftl" as page]

[#assign title="Login"]
[@page.layout]
<p class="container align-center"><a href="/"></a></p>
<div class="widget absolute-center window">
<div class="window-head">
<h1 id="login-head"></h1>
</div>
<div class="container">
<form action="/login" method="post" id="login">
<input name="username" placeholder="Email" autofocus="" type="email">
<input name="password" type="password">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
<div class="btn-container">
<button type="submit" id="loginBtn"><i class="icon i-login at-left"></i></button>
</div>
</form>
</div>
</div>
<script src="/static/js/l10n.min.js"></script>
<script src="/static/js/login.js"></script>
[/@page.layout]
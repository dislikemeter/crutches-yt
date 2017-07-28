[#import "../layout/page.ftl" as page]

[#assign title="Register"]
[@page.layout]
<div class="widget window absolute-center">
<div class="window-head">
<h1>Register</h1></div>
<div class="container">
<form method="POST" action="/">
<input type="email" name="email" placeholder="Your email address" required>
<input type="password" name="password" placeholder="Password" required>
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
<div class="btn-container">
<button type="submit">OK</button>
</div>
</form>
</div>
</div>
[/@page.layout]
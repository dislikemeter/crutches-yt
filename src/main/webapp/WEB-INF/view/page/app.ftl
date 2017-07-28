[#import "../layout/page.ftl" as page]

[#assign title="App"]
[@page.layout]
<div class="full" id="app">
<router-view></router-view>
</div>
[#if debug?has_content]
<script src="//unpkg.com/vue"></script>
[#else]
<script src="//unpkg.com/vue/dist/vue.min.js"></script>
[/#if]
<script src="//unpkg.com/vuex/dist/vuex.min.js"></script>
<script src="//unpkg.com/vue-router/dist/vue-router.min.js"></script>
<script src="//unpkg.com/axios/dist/axios.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.18.1/moment.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.18.1/locale/ru.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/Chart.js/2.6.0/Chart.min.js"></script>
<script src="/static/js/l10n.min.js"></script>
<script src="/static/js/components.min.js"></script>
<script src="/static/js/app.min.js" async></script>
[/@page.layout]
function modalError(e) {
    var errorName = (e && e.response && e.response.status) ? app.msg('errors')[e.response.status] : 'Ajax Error',
        output = (e && e.response && e.response.data) ? e.response.data.message ? app.msg(e.response.data.message) : e.response.data.exception || app.msg('connection-error') : app.msg('connection-error');
    app.$emit('modal', { title: errorName, content: output, buttons: [{ name: 'OK' }] });
}

//XHR Defaults
var xsrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content'),
    xsrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
axios.defaults.headers.common[xsrfHeader] = xsrfToken;
axios.defaults.headers.common['Content-Type'] = 'application/json';
axios.interceptors.response.use(function(response) {
    return response;
}, function(error) {
    switch (error.response.status) {
        //network error
        case undefined:
        case 502:
            modalError();
            break;
        case 403:
            if (error.response.data && error.response.data.exception === 'AuthenticationRequired') {
                window.location.pathname = '/login';
            } else
                modalError(error);
            break;
    }
    return error;
});

//Charts defaults
Chart.defaults.global.elements.point.radius = 0;
Chart.defaults.global.elements.point.hoverRadius = 3;
Chart.defaults.global.elements.point.hitRadius = 15;
Chart.defaults.global.elements.line.borderWidth = 2;
Chart.defaults.global.hover.mode = 'nearest';
Chart.defaults.global.hover.intersect = true;
Chart.defaults.global.tooltips.mode = 'index';
Chart.defaults.global.tooltips.intersect.mode = false;
Chart.defaults.global.responsive = true;
Chart.defaults.global.maintainAspectRatio = false;

//Time format defaults
moment.locale(lang.currentLocale);

//Routes
var routes = [{
    path: '/',
    component: components.sidebarLayout,
    children: [{
        path: '',
        component: components.dashboard
    }, {
        path: 'account',
        component: components.viewProfile
    }, {
        path: 'account/edit',
        component: components.editProfile
    }, {
        path: 'account/password',
        component: components.changePassword
    }, {
        path: 'manage/users',
        component: components.editUsers
    }, {
        path: 'manage/roles',
        component: components.editRoles
    }, {
        path: 'manage/permissions',
        component: components.editPermissions
    }, {
    	path: 'config',
    	component: components.editParams
    }, {
        path: 'ytstats/',
        component: components.ytStats
    }, {
        path: 'ytstats/:videoId',
        component: components.ytStats,
        props: true
    }]
}, {
    path: '*',
    component: components.notFound
}];

//Application
var app = new Vue({
    el: '#app',
    router: new VueRouter({
        routes: routes,
        mode: 'history'
    }),
    data: {},
    methods: {
        msg: lang.msg,
        msgf: function(message) {
            var replacer = function(str, msg) {
                return lang.msg(msg);
            };
            return message.replace(/\${([a-z_-]+)}/gi, replacer);
        },
        setTitle: function(title) {
        	document.title = this.msg(title)||title;
        }
    }
});
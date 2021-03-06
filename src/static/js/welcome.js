var link = document.createElement('link');
link.rel = 'stylesheet';
link.href = '/static/css/welcome.min.css';
var loginBtn = document.getElementById('login');
loginBtn.addEventListener('click', function() {
    location.pathname = '/login';
});
loginBtn.appendChild(document.createTextNode(lang.msg('login')));
var tod, hour = (new Date()).getHours();
switch (hour) {
    case 5:
    case 6:
    case 7:
    case 8:
    case 9: tod = 'morning'; break;
    case 18:
    case 19:
    case 20:
    case 21: tod = 'evening'; break;
    case 22:
    case 23:
    case 0:
    case 1:
    case 2:
    case 3:
    case 4: tod = 'night'; break;
    default: tod = 'day';
}
document.body.className = tod;
document.head.appendChild(link);
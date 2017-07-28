document.getElementById('login-head').innerHTML = lang.msg('login');
document.getElementById('loginBtn').appendChild(document.createTextNode(lang.msg('login')));
document.querySelector('input[name="password"]').setAttribute('placeholder', lang.msg('password'));
document.querySelector('a[href="/"]').innerHTML = lang.msg('main-page');
function message(msg) {
    var p = document.createElement('p');
    p.innerHTML = msg;
    p.className = 'align-center';
    document.getElementById('login').appendChild(p);
}
switch (location.search) {
    case '?logout':
        message(lang.msg('logout-successfull'));
        break;
    case '?error':
    	message(lang.msg('login-error'));
        break;
}
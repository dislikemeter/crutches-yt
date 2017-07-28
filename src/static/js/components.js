//Store
var store = new Vuex.Store({
    state: {
        user: {},
        modules: [],
        tags: null
    },
    mutations: {
        setUser: function(s, p) {
            s.user = p;
        },
        passChange: function(s) {
            s.user.passExpireDate = null;
        },
        setModules: function(s, p) {
            s.modules = p;
        },
        toggleFav: function(s, p) {
            var index = s.modules.indexOf(p.module);
            s.modules[index].fav = !!p.fav;
        },
        loadTags: function(s) {
            if (!s.tags) {
                axios.get('/api/tags').then(function(response) {
                    if (response.data && Array.isArray(response.data)) {
                        s.tags = response.data;
                    }
                });
            }
        },
        addtag: function(s, p) {
            if (!isNaN(p.id) && Array.isArray(s.tags)) {
                for (var i = 0; i < s.tags.length; i++) {
                    if (s.tags[i].id === p.id)
                        return;
                }
                s.tags.push(p);
            }
        }
    }
});

var dashboardIcon = {
    admin: 'i-gear',
    media: 'i-media',
    account: 'i-user'
};

//Components
var components = {
    notFound: Vue.component('not-found', {
        template: '<div class="absolute-center window"><div class="sim-table"><div class="container"><img src="/static/img/elf.png"></div><div class="container"><h1>{{$root.msg(\'not-found\')}}</h1><p>{{$root.msg(\'nothing-here\')}}</p><p><span class="pseudo-link" @click="returnClick">{{$root.msg(\'go-back\')}}</span></p><p><router-link to="/">{{$root.msg(\'main-page\')}}</router-link></p></div></div></div>',
        data: function() {
            return {};
        },
        methods: {
            returnClick: function() {
                this.$router.go(-1);
            }
        },
        mounted: function() {
            this.$root.setTitle('not-found');
        }
    }),
    modalWindow: Vue.component('modal-window', {
        template: '<div id="modal-layer" class="full" :class="{active:isActive}" @scroll="onScroll" @click="onClick"><div class="widget window absolute-center"><div class="window-head"><h1>{{title}}</h1></div><div class="container"><p v-html="content"></p><div class="btn-container"><button v-for="(button, index) in buttons" :class="button.class" @click="exit(index)"><i v-if="button.icon" :class="button.icon + \' icon at-left\'"></i>{{ button.name }}</button></div></div></div></div>',
        data: function() {
            return {
                isActive: false,
                title: '',
                content: '',
                buttons: []
            };
        },
        methods: {
            onScroll: function(evt) {
                evt.preventDefault();
            },
            onClick: function(evt) {
                if (evt.target === this.$el)
                    this.exit(0);
            },
            show: function(evt) {
                if (this.isActive) return;
                this.title = this.$root.msgf(evt.title);
                this.content = this.$root.msgf(evt.content);
                this.buttons = evt.buttons;
                this.buttons.forEach(function(button) {
                    button.name = this.$root.msgf(button.name);
                }, this);
                this.isActive = true;
            },
            exit: function(code) {
                this.$root.$emit('modalExit', code);
                this.isActive = false;
            }
        },
        created: function() {
            this.$root.$on('modal', this.show);
            var me = this;
            this.$root.$on('keydown', function(evt) {
                if (evt.keyCode === 27)
                    me.exit(-1);
            });
        }
    }),
    sideMenu: Vue.component('side-menu', {
        template: '<div><label for="mainmenu"><img src="/static/img/menu.svg"></label><router-link id="logo" to="/" class="clearfix"><img src="/static/img/logo_l.png" alt="logo"></router-link><input id="mainmenu" class="menu" type="checkbox"><div class="mobile-menu"><nav><router-link to="/account"><img class="small avatar" :src="user.avatar" alt="Profile icon"><i v-if="user.passExpireDate" class="red led at-right"></i> {{user.firstName}}</router-link><hr><p v-show="favorites.length===0" class="align-center"><em>{{$root.msg(\'favorites\')}}</em></p><router-link v-for="fav in favorites" :to="fav.path" :key="fav.id"><i id="profile-icon" class="icon at-left" :class="icon(fav.catId)"></i> {{$root.msg(fav.name)}}</router-link><hr><a href="/logout"><i class="icon i-logout at-left"></i> {{$root.msg(\'logout\')}}</a></nav></div></div>',
        data: function() {
            return {};
        },
        computed: {
            user: function() {
                return store.state.user;
            },
            favorites: function() {
                return store.state.modules.filter(function(module) {
                    return module.fav;
                });
            }
        },
        methods: {
            icon: function(catId) {
                return dashboardIcon[catId];
            }
        }
    }),
    sidebarLayout: Vue.component('sidebar-layout', {
        template: '<div class="full"><modal-window/><div id="page" class="full"><aside><side-menu></side-menu></aside><section id="main"><div class="container"><router-view/></div></section></div></div>',
        data: function() {
            return {};
        },
        created: function() {
            axios.get('/api/myprofile').then(function(response) {
                if (response.data) {
                    store.commit('setUser', response.data);
                }
            });
            axios.get('/api/dashboard').then(function(response) {
                if (response.data) {
                    store.commit('setModules', response.data);
                }
            });
        }
    }),
    viewProfile: Vue.component('view-profile', {
        template: '<div><div><h1>{{fullName}}</h1></div><div class="stack mt"><div class="widget window"><div class="window-head"><h2>{{$root.msg(\'profile\')}}</h2></div><div class="container"><div class="align-center"><img class="big avatar" :src="user.avatar" alt="Profile photo"></div><p>Email / login: <strong>{{user.email}}</strong></p><p>{{$root.msg(\'phone\')}}: <strong>{{user.phone}}</strong></p><p><router-link to="/account/edit"><i class="icon i-pen at-left"></i>{{$root.msg(\'edit\')}}</router-link></p></div></div><div class="widget window"><div class="window-head"><h2>{{$root.msg(\'account\')}}</h2></div><div class="container"><p class="employee-position">{{$root.msg(\'role\')}}: <strong>{{user.roleName}}</strong></p><p>{{$root.msg(\'registered\')}} <strong><datetime only="date" :value="user.registeredDate"/></strong></p><p v-if="user.expireDate">{{$root.msg(\'expire\')}} <strong><datetime only="date" :value="user.expireDate"/></strong></p><p>{{$root.msg(\'pass-changed\')}} <strong><datetime only="date" :value="user.passChangeDate"/></strong><span v-if="user.credentialsExpireDate">,<br>{{$root.msg(\'expire\')}} <strong class="errortext"><datetime only="date" :value="user.credentialsExpireDate"/></strong></span></p><p><router-link to="/account/password"><i class="icon i-lock at-left"></i>{{$root.msg(\'change-pass\')}}</router-link></p></div></div></div></div>',
        data: function() {
            return {};
        },
        computed: {
            user: function() {
                return store.state.user;
            },
            fullName: function() {
                var fullName = ((this.user.lastName || '') + ' ' + (this.user.firstName) + ' ' + (this.user.middleName || '')).trim();
                this.$root.setTitle(fullName);
                return fullName;
            }
        }
    }),
    editProfile: Vue.component('edit-profile', {
        template: '<div><div><h1>{{$root.msg(\'edit\')}} {{$root.msg(\'profile\')}}</h1></div><div class="stack mt"><div class="widget window"><div class="window-head"><h2>{{$root.msg(\'first-name\')}}</h2></div><div class="container"><form action="/api/myprofile" method="POST" @submit.prevent="submit"><p>Email: <strong>{{post.email}}</strong></p><input type="text" name="lastName" v-model="post.lastName" :class="{error: errorIn(\'lastName\')}" :placeholder="$root.msg(\'last-name\')"><input type="text" name="firstName" v-model="post.firstName" :class="{error: errorIn(\'firstName\')}" :placeholder="$root.msg(\'first-name\')"><input type="text" name="middleName" v-model="post.middleName" :class="{error: errorIn(\'middleName\')}" :placeholder="$root.msg(\'middle-name\')"><input type="tel" name="phone" v-model="post.phone" :class="{error: errorIn(\'phone\')}" :placeholder="$root.msg(\'phone\')"><div class="btn-container"><button type="submit" :disabled="invalid || blocked"><i class="icon i-disk at-left"></i>Save changes</button></div></form></div></div><div class="widget window"><div class="window-head"><h2>{{$root.msg(\'avatar\')}}</h2></div><div class="container align-center"><img class="big avatar" :src="post.avatar" alt="avatar"></div></div></div><p><router-link to="/account"><i class="icon i-back at-left"></i>{{$root.msg(\'cancel\')}}</router-link></p></div>',
        data: function() {
            return {
                post: {},
                errorFields: [],
                ajaxErrorFields: [],
                blocked: false
            };
        },
        computed: {
            invalid: function() {
                this.errorFields = [];
                if (!/^[a-zа-я]{4,32}$/i.test(this.post.firstName))
                    this.errorFields.push('firstName');
                if (!/^[a-zа-я]{4,32}$/i.test(this.post.lastName))
                    this.errorFields.push('lastName');
                if (!/(^[a-zа-я]{4,32}$)|(^$)/i.test(this.post.middleName))
                    this.errorFields.push('middleName');
                if (!/(^\+?\d{7,20}$)|(^$)/i.test(this.post.phone))
                    this.errorFields.push('phone');
                return !!this.errorFields.length;
            }
        },
        methods: {
            submit: function() {
                if (!this.invalid) {
                    this.blocked = true;
                    this.ajaxErrorFields = [];
                    var me = this;
                    axios.post('/api/myprofile', this.post)
                        .then(function(response) {
                            if (!response.data.modal)
                                throw me.$root.msg('connection-error');
                            me.$root.$on('modalExit', function(exitcode) {
                                me.$root.$off('modalExit');
                                if (exitcode === 1) {
                                    me.$router.push('/account');
                                }
                                me.blocked = false;
                            });
                            store.commit('setUser', me.post);
                            me.$root.$emit('modal', response.data.modal);
                        }).catch(function(e) {
                            if (e.response && e.response.data && e.response.data.errorFields) {
                                me.ajaxErrorFields = e.response.data.errorFields;
                            } else {
                                modalError(e);
                            }
                            me.blocked = false;
                        });
                }
            },
            errorIn: function(field) {
                return this.errorFields.indexOf(field) >= 0 || this.ajaxErrorFields.indexOf(field) >= 0;
            }
        },
        created: function() {
            this.post = JSON.parse(JSON.stringify(store.state.user));
        },
        mounted: function() {
            this.$root.setTitle('profile');
        }
    }),
    changePassword: Vue.component('change-password', {
        template: '<div class="centered widget window"><div class="align-center window-head"><h1>{{$root.msg(\'change-pass\')}}</h1></div><div class="container"><form action="/api/password" method="POST" @submit.prevent="submit"><p>{{$root.msg(\'old-password\')}}</p><input name="password" required autofocus="autofocus" type="password" v-model="post.password" :class="{error: errorIn(\'password\')}"><p>{{$root.msg(\'new-password\')}}</p><input v-if="showPassword" name="new-password" required type="text" v-model="post.newPassword" :class="{error: errorIn(\'newPassword\')}"><input v-else name="new-password" required type="password" v-model="post.newPassword" :class="{error: errorIn(\'newPassword\')}"><label><input type="checkbox" v-model="showPassword"><i></i>{{$root.msg(\'show-password\')}}</label><div class="btn-container"><button type="submit" :disabled="invalid || blocked"><i class="icon i-disk at-left"></i>{{$root.msg(\'save\')}}</button></div><p><router-link to="/account"><i class="icon i-back at-left"></i>{{$root.msg(\'cancel\')}}</router-link></p></form></div></div>',
        data: function() {
            return {
                post: {
                    password: '',
                    newPassword: ''
                },
                showPassword: false,
                errorFields: [],
                ajaxErrorFields: [],
                blocked: false
            };
        },
        computed: {
            invalid: function() {
                this.errorFields = [];
                if (!/^[\w:;.,!@#$%^&*(){}]{8,64}$/.test(this.post.password))
                    this.errorFields.push('password');
                return !!this.errorFields.length;
            },
            inputType: function() {
                return this.showPassword ? 'text' : 'password';
            }
        },
        methods: {
            submit: function() {
                if (!this.invalid) {
                    this.blocked = true;
                    this.ajaxErrorFields = [];
                    var me = this;
                    axios.post('/api/password', this.post)
                        .then(function(response) {
                            if (!response.data.modal)
                                throw me.$root.msg('connection-error');
                            me.$root.$on('modalExit', function(exitcode) {
                                me.$root.$off('modalExit');
                                if (exitcode === 1) {
                                    me.$router.push('/account');
                                }
                                me.blocked = false;
                                me.post.password = '';
                                me.post.newPassword = '';
                            });
                            store.commit('passChange');
                            me.$root.$emit('modal', response.data.modal);
                        }).catch(function(e) {
                            if (e.response && e.response.data && e.response.data.errorFields) {
                                me.ajaxErrorFields = e.response.data.errorFields;
                            } else {
                                modalError(e);
                            }
                            me.blocked = false;
                        });
                }
            },
            errorIn: function(field) {
                return this.errorFields.indexOf(field) >= 0 || this.ajaxErrorFields.indexOf(field) >= 0;
            }
        },
        mounted: function() {
            this.$root.setTitle('change-pass');
        }
    }),
    selectList: Vue.component('select-list', {
        template: '<div class="ui-select"><input v-if="find" v-model="filterString" type="text" :placeholder="searchph || \'...\'"><ul><li v-for="item, index in items" v-show="isMatchingFilter(item)" :class="{active: isActive(index)}" @click="toggleItem(index)"><span>{{item.name ? item.name : item}}</span></li></ul></div>',
        props: ['items', 'multiselect', 'find', 'searchph', 'value'],
        data: function() {
            return {
                filterString: ''
            };
        },
        computed: {
            filterToLower: function() {
                return this.filterString.toLowerCase();
            }
        },
        methods: {
            isActive: function(index) {
                return this.value.indexOf(index) !== -1;
            },
            isMatchingFilter: function(item) {
                if (this.filterString.length === 0) return true;
                var currentName = item.name ? item.name.toLowerCase() : JSON.stringify(item).toLowerCase();
                return (currentName.indexOf(this.filterToLower) !== -1);
            },
            toggleItem: function(index) {
                var result = this.value,
                    arrayIndex = result.indexOf(index);
                if (arrayIndex === -1) {
                    if (this.multiselect)
                        result.push(index);
                    else
                        result = [index];
                } else {
                    if (this.multiselect)
                        result.splice(arrayIndex, 1);
                    else
                        result = [];
                }
                this.$emit('input', result);
            }
        },
        watch: {
            items: function() {
                this.$emit('input', []);
            }
        }
    }),
    editRoles: Vue.component('edit-roles', {
        template: '<div class="stack" :class="{loader:loading}"><div><h2>{{$root.msg(\'roles\')}}</h2><select-list :items="roles" v-model="selectedRoles" @input="roleSelect" :searchph="$root.msg(\'filter\')" find="true"></select-list><input type="text" v-model="roleName" :placeholder="$root.msg(\'name\')"><div class="btn-container"><button :disabled="!roleNameIsValid || roleExists" @click="addRole"><i class="icon i-plus at-left"></i>{{$root.msg(\'add\')}}</button><button :disabled="!roleNameIsValid || !selectedRole" @click="saveRole"><i class="icon i-disk at-left"></i>{{$root.msg(\'save\')}}</button><button :disabled="!selectedRole" @click="deleteRole"><i class="icon i-trash at-left"></i>{{$root.msg(\'delete\')}}</button></div></div><div class="widget"><h2>{{$root.msg(\'linked-permissions\')}}</h2><select-list v-model="selectedPermissions" :items="permissions" :searchph="$root.msg(\'filter\')" multiselect="true" find="true"></select-list></div></div>',
        data: function() {
            return {
                roles: [],
                permissions: [],
                selectedRoles: [],
                selectedPermissions: [],
                roleName: '',
                rolesLoading: true,
                permLoading: true
            };
        },
        computed: {
            selectedRole: function() {
                return this.selectedRoles.length === 1 ? this.roles[this.selectedRoles[0]] : null;
            },
            selectedRoleIndex: function() {
                return this.selectedRoles.length === 1 ? this.selectedRoles[0] : null;
            },
            selectedPermissionsIds: function() {
                var me = this;
                return this.selectedPermissions.map(function(selectedIndex) {
                    return me.permissions[selectedIndex].id;
                });
            },
            roleNameIsValid: function() {
                return /^[a-z_]+$/i.test(this.roleName);
            },
            roleExists: function() {
                var me = this;
                return this.roles.some(function(role) {
                    return role.name.toLowerCase === me.roleName.toLowerCase();
                });
            },
            loading: function() {
                return this.rolesLoading || this.permLoading;
            }
        },
        methods: {
            addRole: function() {
                var me = this;
                axios.post('/api/roles', {
                        roleName: this.roleName,
                        linkedPermissions: this.selectedPermissionsIds
                    })
                    .then(function(response) {
                        if (response.data) {
                            me.roles = me.roles.concat(response.data);
                            me.roleName = '';
                        }
                    }).catch(function(e) {
                        modalError(e);
                    });
            },
            saveRole: function() {
                var me = this,
                    selectedRoleIndex = this.selectedRoleIndex;
                axios.post('/api/roles/' + this.selectedRole.id, {
                        roleName: this.roleName,
                        linkedPermissions: this.selectedPermissionsIds
                    })
                    .then(function(response) {
                        if (response.data) {
                            Vue.set(me.roles, selectedRoleIndex, response.data[0]);
                        }
                    }).catch(function(e) {
                        modalError(e);
                    });
            },
            deleteRole: function() {
                var me = this,
                    selectedRoleIndex = this.selectedRoleIndex;
                axios.delete('/api/roles/' + this.selectedRole.id)
                    .then(function(response) {
                        me.roleName = '';
                        me.roles.splice(selectedRoleIndex, 1);
                    }).catch(function(e) {
                        modalError(e);
                    });
            },
            roleSelect: function() {
                var me = this,
                    result = [];
                this.roleName = this.selectedRole ? this.selectedRole.name : '';
                if (this.selectedRole) {
                    this.permissions.forEach(function(permission, index) {
                        if (me.selectedRole.linkedPermissions.indexOf(permission.id) !== -1)
                            result.push(index);
                    });
                }
                this.selectedPermissions = result;
            }
        },
        created: function() {
            var me = this;
            axios.get('/api/permissions').then(function(response) {
                if (response.status === 200 && response.data) {
                    me.permissions = response.data;
                    me.permLoading = false;
                }
            });
            axios.get('/api/roles').then(function(response) {
                if (response.status === 200 && response.data) {
                    me.roles = response.data;
                    me.rolesLoading = false;
                }
            });
        },
        mounted: function() {
            this.$root.setTitle('roles');
        }
    }),
    editPermissions: Vue.component('edit-permissions', {
        template: '<div :class="{loader:loading}"><h2>{{$root.msg(\'permissions\')}}</h2><select-list :items="permissions" v-model="selectedPermissions" @input="permissionSelect" :searchph="$root.msg(\'filter\')" find="true"></select-list><input type="text" v-model="permissionName" :placeholder="$root.msg(\'name\')"><div class="btn-container"><button :disabled="!permissionNameIsValid || permissionExists" @click="addPermission"><i class="icon i-plus at-left"></i>{{$root.msg(\'add\')}}</button><button :disabled="!permissionNameIsValid || !selectedPermission" @click="savePermission"><i class="icon i-disk at-left"></i>{{$root.msg(\'save\')}}</button><button :disabled="!selectedPermission" @click="deletePermission"><i class="icon i-trash at-left"></i>{{$root.msg(\'delete\')}}</button></div></div>',
        data: function() {
            return {
                permissions: [],
                selectedPermissions: [],
                permissionName: '',
                loading: true
            };
        },
        computed: {
            selectedPermission: function() {
                return this.selectedPermissions.length === 1 ? this.permissions[this.selectedPermissions[0]] : null;
            },
            selectedPermissionIndex: function() {
                return this.selectedPermissions.length === 1 ? this.selectedPermissions[0] : null;
            },
            permissionNameIsValid: function() {
                return /^[a-z_]+$/i.test(this.permissionName);
            },
            permissionExists: function() {
                var me = this;
                return this.permissions.some(function(permission) {
                    return permission.name.toLowerCase === me.permissionName.toLowerCase();
                });
            }
        },
        methods: {
            permissionSelect: function() {
                this.permissionName = this.selectedPermission ? this.selectedPermission.name : '';
            },
            addPermission: function() {
                var me = this;
                axios.post('/api/permissions', { permissionName: this.permissionName })
                    .then(function(response) {
                        if (response.data) {
                            me.permissions = me.permissions.concat(response.data);
                            me.permissionName = '';
                        }
                    }).catch(function(e) {
                        modalError(e);
                    });
            },
            savePermission: function() {
                var me = this,
                    selectedPermIndex = this.selectedPermissionIndex;
                axios.post('/api/permissions/' + this.selectedPermission.id, { permissionName: this.permissionName })
                    .then(function(response) {
                        if (response.data) {
                            Vue.set(me.permissions, selectedPermIndex, response.data[0]);
                        }
                    }).catch(function(e) {
                        modalError(e);
                    });
            },
            deletePermission: function() {
                var me = this,
                    selectedPermIndex = this.selectedPermissionIndex;
                axios.delete('/api/permissions/' + this.selectedPermission.id)
                    .then(function(response) {
                        me.permissions.splice(selectedPermIndex, 1);
                        me.permissionName = '';
                    }).catch(function(e) {
                        modalError(e);
                    });
            }
        },
        created: function() {
            var me = this;
            axios.get('/api/permissions').then(function(response) {
                if (response.status === 200 && response.data) {
                    me.permissions = response.data;
                    me.loading = false;
                }
            });
        },
        mounted: function() {
            this.$root.setTitle('permissions');
        }
    }),
    datetime: Vue.component('datetime', {
        template: '<span>{{dateTime}}</span>',
        props: ['value', 'only'],
        computed: {
            dateTime: function() {
                if (!this.value || isNaN(this.value))
                    return '–';
                else
                    return moment(this.value).calendar();
            }
        }
    }),
    datepicker: Vue.component('datepicker', {
        template: '<div class="datepicker" :class="{active:isOpen}" :disabled="disabled" @focus="focus" @blur="blur"><div class="dateholder"><datetime only="date" :value="value"></datetime></div><div v-if="!disabled" class="picker-popup"><div><select class="il" v-model="selectedMonth" @blur="blur"><option v-for="month, index in monthNames" :value="index">{{month}}</option></select><select class="il" v-model="selectedYear" @blur="blur"><option v-for="year in 131" :value="1969+year">{{1969 + year}}</option></select></div><div tabindex="0" class="calendar" @keydown="calendarKey" @blur="blur"><span v-for="n in daysGap"></span><span v-for="day in daysCount" class="day" :class="{active: day === selectedDay, current: isCurrent(day)}" @click="selectedDay = day">{{day}}</span></div><p class="align-center pseudo-link" @click="selectCurrent">{{$root.msg(\'today\')}}</p></div></div>',
        props: ['value', 'disabled'],
        data: function() {
            return {
                monthNames: this.$root.msg('months'),
                selectedDate: new Date(0),
                currentDate: new Date(),
                firstDay: this.$root.msg('firstDay'),
                isOpen: false
            };
        },
        watch: {
            value: function(newValue) {
                newValue = newValue || new Date();
                this.selectedDate = newValue.getTime ? newValue : typeof newValue === 'number' ? new Date(newValue) : new Date(parseInt(newValue));
            },
            disabled: function(newValue) {
                if (!newValue)
                    this.$el.setAttribute('tabindex', 0);
                else
                    this.$el.removeAttribute('tabindex');
            }
        },
        computed: {
            selectedDay: {
                get: function() {
                    return this.selectedDate.getDate();
                },
                set: function(newValue) {
                    var newDate = new Date(this.selectedDate.valueOf());
                    newDate.setDate(newValue);
                    this.$emit('input', newDate.valueOf());
                }
            },
            selectedMonth: {
                get: function() {
                    return this.selectedDate.getMonth();
                },
                set: function(newValue) {
                    var newDate = new Date(this.selectedDate.valueOf());
                    newDate.setMonth(newValue);
                    this.$emit('input', newDate.valueOf());
                }
            },
            selectedYear: {
                get: function() {
                    return this.selectedDate.getFullYear();
                },
                set: function(newValue) {
                    var newDate = new Date(this.selectedDate.valueOf());
                    newDate.setFullYear(newValue);
                    this.$emit('input', newDate.valueOf());
                }
            },
            daysGap: function() {
                var firstDate = new Date(this.selectedDate.valueOf());
                firstDate.setDate(1);
                var daysGap = firstDate.getDay() - this.firstDay;
                if (daysGap < 0) daysGap += 7;
                return daysGap;
            },
            daysCount: function() {
                switch (this.selectedMonth) {
                    case 1:
                        return this.selectedYear % 400 === 0 ? 29 :
                            this.selectedYear % 100 === 0 ? 28 :
                            this.selectedYear % 4 === 0 ? 29 : 28;
                    case 3:
                        return 30;
                    case 5:
                        return 30;
                    case 8:
                        return 30;
                    case 10:
                        return 30;
                    default:
                        return 31;
                }
            },
            currentDay: function() {
                return this.currentDate.getDate();
            },
            currentMonth: function() {
                return this.currentDate.getMonth();
            },
            currentYear: function() {
                return this.currentDate.getFullYear();
            }
        },
        methods: {
            selectCurrent: function() {
                if (this.disabled) return;
                this.$emit('input', this.currentDate.valueOf());
                this.$el.blur();
            },
            isCurrent: function(day) {
                return this.selectedDate.getFullYear() === this.currentDate.getFullYear() &&
                    this.selectedDate.getMonth() === this.currentDate.getMonth() &&
                    day === this.currentDate.getDate();
            },
            calendarKey: function(evt) {
                var date = this.selectedDate,
                    increment = 0;
                switch (evt.keyCode) {
                    case 37:
                        increment = -1;
                        break;
                    case 38:
                        increment = -7;
                        break;
                    case 39:
                        increment = 1;
                        break;
                    case 40:
                        increment = 7;
                        break;
                }
                date.setDate(date.getDate() + increment);
                this.$emit('input', date.valueOf());
            },
            focus: function() {
                this.isOpen = true;
            },
            blur: function() {
                var me = this;
                setTimeout(function() {
                    me.isOpen = !!(me.$el === document.activeElement || me.$el.querySelector(':focus'));
                }, 100);
            }
        },
        mounted: function() {
            if (!this.disabled)
                this.$el.setAttribute('tabindex', 0);
            else
                this.$el.removeAttribute('tabindex');
        }
    }),
    editUsers: Vue.component('edit-users', {
        template: '<div :class="{loader:loading}"><div><h1>{{$root.msg(\'edit-users\')}}</h1></div><div class="table-container mt"><div><table><thead><tr><th class="col-half"><div>{{$root.msg(\'first-name\')}}</div></th><th><div>Email</div></th><th><div>{{$root.msg(\'role\')}}</div></th><th><div>{{$root.msg(\'active\')}}</div></th><th><div>{{$root.msg(\'delete\')}}</div></th></tr></thead><tbody><tr class="clickable" v-for="user, index in users" @click="selectUserForEdit(user, index)"><td>{{(user.lastName ? user.lastName + \' \' : \'\') + user.firstName + \' \' + (user.middleName ? user.middleName + \' \' : \'\') | trim}}</td><td>{{user.email}}</td><td>{{roleName(user.roleId)}}</td><td class="align-center"><i class="icon" :class="{\'i-ok\': user.enabled, \'i-cancel\': !user.enabled}"></i></td><td class="align-center"><i class="clickable icon i-trash" @click="deleteUser(index)"></i></td></tr></tbody></table></div></div><div class="mt window"><div class="window-head"><h2>{{createMode ? $root.msg(\'new-account\') : $root.msg(\'edit\') + \': \' + editUser.email}}</h2></div><div class="container stack"><div class="widget"><h2>{{$root.msg(\'account\')}}</h2><input type="email" v-model="editUser.email" placeholder="Email" required><select v-model="editUser.roleId"><option v-for="role in roles" :value="role.id">{{role.name}}</option></select><label><input type="checkbox" v-model="editUser.enabled"><i></i>{{$root.msg(\'active\')}}</label><label><input type="checkbox" :checked="!!editUser.expireDate" @change="setAccountExpire"><i></i>{{$root.msg(\'expire\')}}: </label><datepicker v-show="!!editUser.expireDate" v-model="editUser.expireDate"></datepicker></div><div class="widget"><h2>{{$root.msg(\'profile\')}}</h2><input type="text" v-model="editUser.firstName" :placeholder="$root.msg(\'first-name\')" required><input type="text" v-model="editUser.lastName" :placeholder="$root.msg(\'last-name\')"><input type="text" v-model="editUser.middleName" :placeholder="$root.msg(\'middle-name\')"><input type="text" v-model="editUser.phone" :placeholder="$root.msg(\'phone\')"><div class="btn-container"><button @click="save"><i class="icon i-disk at-left"></i>{{$root.msg(\'save\')}}</button><button :disabled="createMode" @click="selectUserForEdit(null)"><i class="icon i-cancel at-left"></i>{{$root.msg(\'cancel\')}}</button></div></div><div v-show="!createMode" class="widget"><h2>{{$root.msg(\'actions\')}}</h2><p class="pseudo-link" @click="reset"><i class="icon i-lock at-left"></i>{{$root.msg(\'reset-pass\')}}</p><p class="pseudo-link" @click="expire"><i class="icon i-logout at-left"></i>{{$root.msg(\'logout\')}}</p><h2>{{$root.msg(\'stats\')}}</h2><p>{{$root.msg(\'registered\')}}: <datetime only="date" :value="editUser.registeredDate"></datetime></p><p>{{$root.msg(\'pass-changed\')}}: <datetime only="date" :value="editUser.passChangeDate"></datetime></p><p>{{$root.msg(\'pass-expire\')}}: <datetime only="date" :value="editUser.passExpireDate"></datetime></p></div></div></div></div>',
        data: function() {
            return {
                users: [],
                roles: [],
                currentUserIndex: null,
                editUser: {},
                rolesLoading: true,
                usersLoading: false
            };
        },
        computed: {
            createMode: function() {
                return this.currentUserIndex === null;
            },
            loading: function() {
                return this.rolesLoading || this.usersLoading;
            }
        },
        methods: {
            newUser: function() {
                this.currentUserIndex = null;
                this.editUser.email = '';
                this.editUser.roleId = null;
                this.editUser.enabled = true;
                this.editUser.expireDate = null;
                this.editUser.firstName = '';
                this.editUser.lastName = '';
                this.editUser.middleName = '';
                this.editUser.phone = '';
            },
            selectUserForEdit: function(user, index) {
                if (user) {
                    this.currentUserIndex = index;
                    this.editUser = JSON.parse(JSON.stringify(user));
                } else {
                    this.newUser();
                }
            },
            roleName: function(roleId) {
                for (var i = 0; i < this.roles.length; i++) {
                    if (this.roles[i].id === roleId) return this.roles[i].name;
                }
            },
            setAccountExpire: function(evt) {
                this.editUser.expireDate = evt.target.checked ? ((new Date()).getTime() + 86400000) : null;
            },
            save: function() {
                var me = this,
                    currentUserIndex = this.currentUserIndex;
                if (this.createMode) {
                    axios.post('/api/users', this.editUser)
                        .then(function(response) {
                            me.users = me.users.concat(response.data);
                            me.newUser();
                        })
                        .catch(function(e) {
                            if (e.response && e.response.data && e.response.data.errorFields) {
                                me.ajaxErrorFields = e.response.data.errorFields;
                                modalError(e);
                            } else {
                                modalError(e);
                            }
                        });
                } else {
                    axios.post('/api/users/' + this.editUser.id, this.editUser)
                        .then(function(response) {
                            me.users[currentUserIndex] = response.data[0];
                            me.newUser();
                        })
                        .catch(function(e) {
                            if (e.response && e.response.data && e.response.data.errorFields) {
                                me.ajaxErrorFields = e.response.data.errorFields;
                                modalError(e);
                            } else {
                                modalError(e);
                            }
                        });
                }
            },
            deleteUser: function(index) {
                var me = this;
                axios.delete('/api/users/' + this.users[index].id)
                    .then(function(response) {
                        me.users.splice(index, 1);
                    })
                    .catch(function(e) {
                        if (e.response && e.response.data && e.response.data.errorFields) {
                            me.ajaxErrorFields = e.response.data.errorFields;
                            modalError(e);
                        } else {
                            modalError(e);
                        }
                    });
            },
            reset: function() {
                axios.post('/api/users/resetpass/' + this.editUser.id, {});
            },
            expire: function() {
                axios.post('/api/users/expire/' + this.editUser.id, {});
            }
        },
        filters: {
            trim: function(str) {
                return str.trim();
            }
        },
        created: function() {
            this.newUser();
            var me = this;
            axios.get('/api/roles')
                .then(function(response) {
                    if (response.status === 200 && response.data) {
                        me.roles = response.data;
                        me.rolesLoading = false;
                    }
                });
            axios.get('/api/users').then(function(response) {
                if (response.status === 200 && response.data) {
                    me.users = response.data;
                    me.usersLoading = false;
                }
            });
        },
        mounted: function() {
            this.$root.setTitle('edit-users');
        }
    }),
    carousel: Vue.component('carousel', {
        template: '<div class="carousel" @mouseenter="update"><div v-if="!noButtons"><div class="like-button left" @click="scrollLeft" :disabled="disableLeft"><i class="icon i-left"></i></div><div class="like-button right" @click="scrollRight" :disabled="disableRight"><i class="icon i-right"></i></div></div><div class="cutter" @wheel.prevent="wheel"><div class="row" :class="{spaced: spaced}" :style="styleObject" ref="row"><slot></slot></div></div></div>',
        props: ['noButtons', 'initScroll', 'spaced'],
        data: function() {
            return {
                scroll: 0,
                disableLeft: true,
                disableRight: false
            };
        },
        computed: {
            styleObject: function() {
                return {
                    transform: 'translateX(-' + this.scroll + 'px)'
                };
            }
        },
        methods: {
            scrollOffset: function(offset) {
                var minScroll = 0,
                    maxScroll = this.$refs.row.scrollWidth - this.$el.clientWidth;
                this.scroll = Math.max(Math.min(this.scroll + offset, maxScroll), minScroll);
                this.disableLeft = (this.scroll <= minScroll);
                this.disableRight = (this.scroll >= maxScroll);
            },
            scrollLeft: function() {
                this.scrollOffset(-(this.$el.clientWidth / 1.5));
            },
            scrollRight: function() {
                this.scrollOffset(this.$el.clientWidth / 1.5);
            },
            wheel: function(evt) {
                var distance = Math.min(Math.max(evt.deltaY > 50 ? evt.deltaY * 3 : evt.deltaY * 100, -300), 300);
                this.scrollOffset(distance);
            },
            update: function() {
                this.scrollOffset(0);
            }
        },
        mounted: function() {
            if (this.initScroll)
                this.scrollOffset(this.initScroll);
        }
    }),
    taglist: Vue.component('taglist', {
        template: '<div class="il"><span v-if="!readonly" class="tag"><label><i class="icon i-plus"></i><input class="il ml" :style="newTagWidth" v-model="name" @change="addTag" maxlength=32></label></span><span v-for="tag in actualTags" class="tag" @click="tagClick(tag)"><em class="mr">{{tag.name}}</em><i v-if="!readonly" class="icon i-cancel" @click.stop="unbindTag(tag.id)"></i></span></div>',
        props: ['readonly', 'tags'],
        data: function() {
            return {
                name: ''
            };
        },
        computed: {
            newTagWidth: function() {
                return { width: ((1 + this.name.length) * 0.5) + 'em' };
            },
            actualTags: function() {
                store.commit('loadTags');
                if (!store.state.tags) return;
                var me = this;
                return store.state.tags.filter(function(tag) {
                    return me.tags.indexOf(tag.id) > -1;
                });
            }
        },
        methods: {
            addTag: function() {
                if (this.readonly) return;
                if (this.name.length > 0) {
                    this.$emit('addtag', this.name);
                    this.name = '';
                }
            },
            unbindTag: function(id) {
                if (this.readonly) return;
                this.$emit('unbindtag', id);
            },
            tagClick: function(tag) {
                this.$emit('tagclick', tag);
            }
        }
    }),
    ytStats: Vue.component('ytstats', {
        template: '<div :class="{loader:loading}"><div class="clearfix" ref="heading"><h1>{{$root.msg(\'ytstats\')}}</h1><div class="il at-right"><label class="icon il"><i class="icon" :class="!filter.length ? \'i-filter\' : \'i-cancel\'" @click="clearFilter"></i><input v-model.lazy="filter" :placeholder="$root.msg(\'filter\')" :class="{\'on-demand\': !filter.length}"></label><label class="icon il ml"><i class="icon i-plus"></i><input v-model.lazy="newVideoUrl" :placeholder="$root.msg(\'add-video\')"></label></div></div><div class="mt"><carousel ref="carousel"><div v-for="item, index in items" v-show="isMatchingFilter(item)" class="yt-stat align-center" :class="{active: currentVideo ? item.id===currentVideo.id : false}"><div class="rec-indicator" v-show="item.watched"><i class="icon i-rec"></i></div><img :src="thumbs[index]" class="clickable" @click="seeStats(index)"><div v-if="item.control" class="yt-btn btn-container"><button @click="toggleWatching(index)"><i class="icon" :class="item.watched ? \'i-stop\' : \'i-rec\'"></i></button></div></div></carousel></div><hr><div v-show="!!currentVideo" class="mt" ref="chart"><div class="clearfix"><h2><i class="icon i-stat"></i> {{$root.msg(\'stats\')}}</h2><div class="at-right"><select class="mr il" v-model.number="tension"><optgroup :label="$root.msg(\'line-mode\')"><option value="0">{{$root.msg(\'polygonal-chain\')}}</option></optgroup><optgroup :label="$root.msg(\'spline-curve\')"><option selected value="0.1">20%</option><option value="0.2">40%</option><option value="0.3">60%</option><option value="0.4">80%</option><option value="0.5">100%</option></optgroup></select><select class="mr il" v-model.number="approx"><optgroup :label="$root.msg(\'approx\')"><option value="0">10 sec</option><option value="3" :disabled="statInterval < 2">30 sec</option><option value="6" :disabled="statInterval < 5">1 min</option><option value="18" :disabled="statInterval < 15">3 min</option><option value="30" :disabled="statInterval < 30">5 min</option><option value="60" :disabled="statInterval < 60">10 min</option></optgroup></select><select class="mr il" v-model="mode"><option value="absolute">{{$root.msg(\'chart-mode\')[0]}}</option><option value="delta">{{$root.msg(\'chart-mode\')[1]}}</option></select><button :disabled="!videoId" @click="updateCurrentChart"><i class="icon i-refresh at-left"></i>{{$root.msg(\'update\')}}</button></div></div><div class="chart"><canvas ref="canvas" id="chart"></canvas></div><div class="mt"><h2>{{$root.msg(\'video\')}}</h2></div><div class="mt clearfix"><div class="at-right" v-if="currentVideo ? currentVideo.control : false"><label class="il mr"><input type="checkbox" v-model="sharedMode">{{$root.msg(\'open-stat\')}}<i class="ml"></i></label><button class="red" @click="deleteVideo"><i class="icon i-trash at-left"></i>{{$root.msg(\'delete\')}}</button></div><p>{{$root.msg(\'added-person\')}}<span class="ml mr">{{currentVideo ? currentVideo.owner : \'\'}}</span><datetime :value="currentVideo ? currentVideo.date : null" /></p><p><taglist :readonly="currentVideo ? !currentVideo.control : true" :tags="currentVideo ? currentVideo.tags : []" @addtag="addTag" @unbindtag="unbindTag" @tagclick="setFilterTag" /></p></div><div class="videowrap mt"><iframe :src="embedUrl"></iframe></div></div></div>',
        props: ['videoId'],
        data: function() {
            return {
                items: null,
                chart: null,
                newVideoUrl: '',
                loading: true,
                tension: 0.1,
                filter: '',
                mode: 'absolute',
                approx: 0,
                fetchedData: {
                    timestamps: [],
                    likeCount: [],
                    dislikeCount: [],
                    commentCount: [],
                    viewCount: []
                }
            };
        },
        computed: {
            thumbs: function() {
                return this.items.map(function(item) {
                    return 'https://img.youtube.com/vi/' + item.videoId + '/mqdefault.jpg';
                });
            },
            invalidLink: function() {
                return !/^https?:\/\/(www\.)?youtube\.com\/watch\?(.*&)?v=([a-zA-Z0-9_-]{10,12}).*$/.test(this.newVideoUrl);
            },
            currentVideo: function() {
                if (!isNaN(this.videoId) && this.videoId > 0 && Array.isArray(this.items)) {
                    for (var i = this.items.length - 1; i >= 0; i--) {
                        if (this.items[i].id == this.videoId) {
                            return this.items[i];
                        }
                    }
                }
            },
            embedUrl: function() {
                return !!this.currentVideo ? 'https://www.youtube.com/embed/' + this.currentVideo.videoId : '';
            },
            sharedMode: {
                get: function() {
                    return this.currentVideo ? this.currentVideo.shared : false;
                },
                set: function(newValue) {
                    var video = this.currentVideo;
                    axios.post('/api/ytstats/' + this.currentVideo.id + '/share', { state: newValue })
                        .then(function(response) {
                            video.shared = newValue;
                        });
                }
            },
            statInterval: function() {
                if (this.fetchedData.timestamps.length < 2)
                    return 0;
                return Math.round((this.fetchedData.timestamps[this.fetchedData.timestamps.length - 1] - this.fetchedData.timestamps[0]) / 60000);
            },
            processedData: function() {
                var temporaryData,
                    deltaFunction = function(value, index, array) {
                        return value - (array[index - 1] || value);
                    },
                    approxFunction = function(array, approx) {
                        if (!Array.isArray(array) || array.length == 0)
                            return [];
                        var result = [];
                        result.push(array[0]);
                        for (var i = 0; i < array.length; i += approx) {
                            var current = 0,
                                j;
                            for (j = 0; j < approx; j++) {
                                if (isNaN(array[i + j])) break;
                                current += array[i + j];
                            }
                            current = Math.round((current / j) * 100) / 100;
                            result.push(current);
                        }
                        result.push(array[array.length - 1]);
                        return result;
                    },
                    skipFunction = function(array, count) {
                        if (!Array.isArray(array) || array.length == 0 || count < 2)
                            return [];
                        var result = [];
                        result.push(array[0]);
                        if (count > 2) {
                            var gap = ((array.length - 1) / (count - 1));
                            for (var i = gap; Math.round(i) < (array.length - 1); i += gap) {
                                result.push(array[Math.round(i)]);
                            }
                        }
                        result.push(array[array.length - 1]);
                        return result;
                    };
                switch (this.mode) {
                    case 'delta':
                        var likeDelta = this.fetchedData.likeCount.map(deltaFunction),
                            dislikeDelta = this.fetchedData.dislikeCount.map(deltaFunction),
                            commentDelta = this.fetchedData.commentCount.map(deltaFunction),
                            viewDelta = this.fetchedData.viewCount.map(deltaFunction);
                        temporaryData = { timestamps: this.fetchedData.timestamps, likeCount: likeDelta, dislikeCount: dislikeDelta, commentCount: commentDelta, viewCount: viewDelta };
                        break;
                    default:
                        temporaryData = this.fetchedData;
                        break;
                }
                if (this.approx) {
                    var likeApprox = approxFunction(temporaryData.likeCount, this.approx),
                        dislikeApprox = approxFunction(temporaryData.dislikeCount, this.approx),
                        commentApprox = approxFunction(temporaryData.commentCount, this.approx),
                        viewApprox = approxFunction(temporaryData.viewCount, this.approx),
                        timestampsSkipped = skipFunction(temporaryData.timestamps, likeApprox.length);
                    temporaryData = { timestamps: timestampsSkipped, likeCount: likeApprox, dislikeCount: dislikeApprox, commentCount: commentApprox, viewCount: viewApprox };
                }
                return temporaryData;
            },
            lastTimestamp: function() {
                return this.fetchedData.timestamps.length > 0 ? Math.max.apply(null, this.fetchedData.timestamps) : 0;
            }
        },
        methods: {
            clearFilter: function() {
                this.filter = '';
            },
            loadDataToChart: function() {
                this.chart.data.labels = this.processedData.timestamps;
                this.chart.data.datasets[0].data = this.processedData.commentCount;
                this.chart.data.datasets[1].data = this.processedData.dislikeCount;
                this.chart.data.datasets[2].data = this.processedData.likeCount;
                this.chart.data.datasets[3].data = this.processedData.viewCount;
                this.chart.update(0);
            },
            isMatchingFilter: function(item) {
                if (this.filter.length < 1 || !store.state.tags)
                    return true;
                var joinedTags = item.tags.map(function(tagId) {
                    for (var i = 0; i < store.state.tags.length; i++) {
                        if (store.state.tags[i].id == tagId)
                            return store.state.tags[i].name;
                    }
                    return '';
                }).join('').toLowerCase();
                return (joinedTags.indexOf(this.filter.toLowerCase()) > -1);
            },
            toggleWatching: function(index) {
                var me = this;
                axios.post('/api/ytstats/' + this.items[index].id, { status: !me.items[index].watched }).then(function(response) {
                    if (response.data)
                        me.items[index].watched = response.data.capturing;
                });
            },
            loadChart: function(videoId) {
                if (isNaN(videoId)) {
                    this.$router.push('/ytstats');
                    return;
                }
                this.approx = 0;
                var me = this;
                axios.get('/api/ytstats/' + videoId).then(function(response) {
                    if (response.data) {
                        me.fetchedData = response.data;
                        me.loadDataToChart();
                    }
                }).catch(function() {
                    me.$router.push('/ytstats');
                });
            },
            updateCurrentChart: function() {
                var me = this;
                axios.get('/api/ytstats/' + this.videoId + '?from=' + this.lastTimestamp).then(function(response) {
                    if (response.data && response.data.timestamps.length > 0) {
                        me.fetchedData.timestamps = me.fetchedData.timestamps.concat(response.data.timestamps);
                        me.fetchedData.likeCount = me.fetchedData.likeCount.concat(response.data.likeCount);
                        me.fetchedData.dislikeCount = me.fetchedData.dislikeCount.concat(response.data.dislikeCount);
                        me.fetchedData.commentCount = me.fetchedData.commentCount.concat(response.data.commentCount);
                        me.fetchedData.viewCount = me.fetchedData.viewCount.concat(response.data.viewCount);
                        me.loadDataToChart();
                    }
                });
            },
            seeStats: function(index) {
                if (this.items[index].id == this.videoId)
                    this.$refs.chart.scrollIntoView();
                else
                    this.$router.push('/ytstats/' + this.items[index].id);
            },
            addTag: function(name) {
                var me = this;
                axios.post('/api/ytstats/' + this.currentVideo.id + '/tag', { action: 'bind', name: name })
                    .then(function(response) {
                        if (response.data && !isNaN(response.data.id)) {
                            store.commit('addtag', response.data);
                            if (me.currentVideo.tags.indexOf(response.data.id) === -1)
                                me.currentVideo.tags.push(response.data.id);
                        }
                    });
            },
            unbindTag: function(id) {
                var me = this;
                axios.post('/api/ytstats/' + this.currentVideo.id + '/tag', { action: 'unbind', tag: id })
                    .then(function(response) {
                        if (response.data) {
                            me.currentVideo.tags.splice(me.currentVideo.tags.indexOf(id), 1);
                        }
                    });
            },
            deleteVideo: function() {
                var me = this;
                axios.delete('/api/ytstats/' + this.currentVideo.id)
                    .then(function(response) {
                        if (response.data) {
                            me.items.splice(me.items.indexOf(me.currentVideo), 1);
                        }
                    });
            },
            setFilterTag: function(tag) {
                this.filter = tag.name;
                this.filterItems();
                this.$refs.heading.scrollIntoView();
            }
        },
        watch: {
            videoId: function(newId) {
                this.loadChart(newId);
            },
            tension: function(value) {
                if (this.chart) {
                    this.chart.options.elements.line.tension = value;
                    this.chart.update();
                }
            },
            newVideoUrl: function(url) {
                if (url.length < 1) return;
                var me = this;
                axios.post('/api/ytstats', { url: url }).then(function(response) {
                    var responseVideo = response.data;
                    responseVideo.watched = false;
                    me.items.unshift(responseVideo);
                    me.newVideoUrl = '';
                });
            },
            mode: function() {
                this.loadDataToChart();
            },
            approx: function() {
                this.loadDataToChart();
            }
        },
        mounted: function() {
            this.$root.setTitle('ytstats');
            var ctx = this.$refs.canvas.getContext('2d'),
                me = this;
            this.chart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: [0],
                    datasets: [{
                        label: "Comments",
                        backgroundColor: 'rgba(255, 152, 0,.2)',
                        borderColor: 'rgb(255, 152, 0)',
                        fill: true,
                        data: [0]
                    }, {
                        label: "Dislikes",
                        backgroundColor: 'rgba(244, 67, 54,.2)',
                        borderColor: 'rgb(244, 67, 54)',
                        fill: true,
                        data: [0]
                    }, {
                        label: "Likes",
                        backgroundColor: 'rgba(3, 169, 244,.2)',
                        borderColor: 'rgb(3, 169, 244)',
                        fill: true,
                        data: [0]
                    }, {
                        label: "Views",
                        backgroundColor: 'rgba(158, 158, 158,.2)',
                        borderColor: 'rgb(158, 158, 158)',
                        hidden: true,
                        fill: true,
                        data: [0]
                    }]
                },
                options: {
                    tooltips: {
                        callbacks: {
                            title: function(item, chart) {
                                return moment(item[0].xLabel).calendar();
                            }
                        }
                    },
                    scales: {
                        xAxes: [{
                            type: 'time'
                        }],
                        yAxes: [{
                            startAtZero: true
                        }]
                    },
                    elements: {
                        line: {
                            tension: me.tension
                        }
                    }
                }
            });
            axios.get('/api/ytstats').then(function(response) {
                me.items = response.data.reverse();
                me.loading = false;
            });
            if (this.videoId) {
                this.loadChart(this.videoId);
            }
        }
    }),
    dashboard: Vue.component('dashboard', {
        template: '<div><div><h1>{{$root.msg(\'dashboard\')}}</h1></div><div class="stack mt"><div v-for="moduleSet, catId in dashboard" class="widget"><h2>{{$root.msg(\'mod-cat\')[catId]}}</h2><div v-for="module in moduleSet" class="mt module"><i class="clickable icon at-left" :class="module.fav ? \'i-fav\' : \'i-nofav\'" @click="toggleFav(module)"></i><router-link :to="module.path">{{$root.msg(module.name)}}</router-link></div></div></div></div>',
        data: function() {
            return {};
        },
        computed: {
            dashboard: function() {
                var result = {};
                store.state.modules.forEach(function(module) {
                    if (!result.hasOwnProperty(module.catId)) {
                        result[module.catId] = [];
                    }
                    result[module.catId].push(module);
                }, this);
                return result;
            }
        },
        methods: {
            toggleFav: function(module) {
                var me = this;
                axios.post('/api/dashboard/' + module.id, {}).then(function(response) {
                    store.commit('toggleFav', { module: module, fav: response.data.fav });
                });
            }
        },
        mounted: function() {
            this.$root.setTitle('dashboard');
        }
    }),
    editParams: Vue.component('edit-params', {
        template: '<div :class="{loader:loading}"><div><h1>{{$root.msg(\'config\')}}</h1></div><div class="table-container mt"><div><table><thead><tr><th class="col-half"><div>{{$root.msg(\'name\')}}</div></th><th><div>{{$root.msg(\'value\')}}</div></th><th class="col-small"></th></tr></thead><tbody><tr v-for="param, index in params"><td>{{param.name}}</td><td><input v-model="param.value" @change="postParam(index)"></td><td class="align-center"><i class="clickable icon i-trash" @click="deleteParam(index)"></i></td></tr></tbody></table></div></div><div class="widget mt"><h2>{{$root.msg(\'add\')}}</h2><input v-model="newName" :placeholder="$root.msg(\'name\')"><input v-model="newValue" :placeholder="$root.msg(\'value\')"><div class="btn-container"><button :disabled="invalid" @click="addParam"><i class="icon i-plus at-left"></i>{{$root.msg(\'add\')}}</button></div></div></div>',
        data: function() {
            return {
                params: [],
                newName: '',
                newValue: '',
                loading: true
            };
        },
        created: function() {
            var me = this;
            axios.get('/api/config').then(function(response) {
                if (response.data)
                    me.params = response.data;
                me.loading = false;
            }).catch(function(e) {
                me.loading = false;
            });
        },
        methods: {
            postParam: function(index) {
                if (index < this.params.length) {
                    axios.post('/api/config', { name: this.params[index].name, value: this.params[index].value });
                }
            },
            deleteParam: function(index) {
                var me = this;
                axios.delete('/api/config/' + this.params[index].id).then(function(response) {
                    if (response.data) me.params.splice(index, 1);
                });
            },
            addParam: function() {
                var me = this;
                axios.post('/api/config', { name: this.newName, value: this.newValue }).then(function(response) {
                    if (response.data && response.data.id) {
                        me.params.push(response.data);
                        me.newName = '';
                        me.newValue = '';
                    }
                });
            }
        },
        computed: {
            invalid: function() {
                return !(this.newName.length && this.newValue.length);
            }
        },
        mounted: function() {
            this.$root.setTitle('config');
        }
    })
};
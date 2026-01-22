import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/Login.vue'),
        meta: { guest: true }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('../views/Register.vue'),
        meta: { guest: true }
    },
    {
        path: '/',
        component: () => import('../layouts/MainLayout.vue'),
        meta: { requiresAuth: true },
        children: [
            {
                path: '',
                name: 'Dashboard',
                component: () => import('../views/Dashboard.vue')
            },
            {
                path: 'courses',
                name: 'Courses',
                component: () => import('../views/student/CourseList.vue'),
                meta: { role: 'STUDENT' }
            },
            {
                path: 'my-registrations',
                name: 'MyRegistrations',
                component: () => import('../views/student/MyRegistrations.vue'),
                meta: { role: 'STUDENT' }
            },
            {
                path: 'admin/semesters',
                name: 'AdminSemesters',
                component: () => import('../views/admin/Semesters.vue'),
                meta: { role: 'ADMIN' }
            },
            {
                path: 'admin/courses',
                name: 'AdminCourses',
                component: () => import('../views/admin/Courses.vue'),
                meta: { role: 'ADMIN' }
            },
            {
                path: 'admin/subjects',
                name: 'AdminSubjects',
                component: () => import('../views/admin/Subjects.vue'),
                meta: { role: 'ADMIN' }
            },
            {
                path: 'lecturer/courses',
                name: 'LecturerCourses',
                component: () => import('../views/lecturer/LecturerCourses.vue'),
                meta: { role: 'LECTURER' }
            },
            {
                path: 'admin/users',
                name: 'AdminUsers',
                component: () => import('../views/admin/Users.vue'),
                meta: { role: 'ADMIN' }
            },
            {
                path: 'admin/registration-periods',
                name: 'AdminRegistrationPeriods',
                component: () => import('../views/admin/RegistrationPeriods.vue'),
                meta: { role: 'ADMIN' }
            },
            {
                path: 'schedule',
                name: 'StudentSchedule',
                component: () => import('../views/student/Schedule.vue'),
                meta: { role: 'STUDENT' }
            }
        ]
    }
]
const router = createRouter({
    history: createWebHistory(),
    routes
})
router.beforeEach((to, from, next) => {
    const authStore = useAuthStore()
    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        next('/login')
    } else if (to.meta.guest && authStore.isAuthenticated) {
        next('/')
    } else {
        next()
    }
})
export default router

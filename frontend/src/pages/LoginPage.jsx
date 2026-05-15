import { useState } from 'react';
import { loginRequest } from '../api/auth';
import { Link } from 'react-router-dom';

export default function LoginPage() {
    const [formData, setFormData] = useState({ nameOrEmail: '', password: '' });
    const [error, setError] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const user = await loginRequest(formData);
            console.log("Bienvenido, operario de Chronos:", user);
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div className="min-h-screen bg-[radial-gradient(ellipse_at_bottom_left,_var(--tw-gradient-stops))] from-slate-900 via-slate-900 to-indigo-950 flex items-center justify-center p-4">
            <div className="w-full max-w-md">
                {/* Logo / Header */}
                <div className="text-center mb-8">
                    <h1 className="text-4xl font-extrabold text-white tracking-tighter">
                        CHRONOS<span className="text-indigo-500">LINK</span>
                    </h1>
                    <p className="text-slate-400 mt-2 font-medium">Protocolo de Acceso Temporal</p>
                </div>

                {/* Card */}
                <div className="bg-slate-800/50 backdrop-blur-xl border border-slate-700 p-8 rounded-3xl shadow-2xl">
                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div>
                            <label className="block text-sm font-semibold text-slate-300 mb-2">Usuario o Email</label>
                            <input
                                type="text"
                                className="w-full bg-slate-900/50 border border-slate-600 rounded-xl px-4 py-3 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                                placeholder="identificador_alpha"
                                onChange={(e) => setFormData({ ...formData, nameOrEmail: e.target.value })}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-semibold text-slate-300 mb-2">Contraseña</label>
                            <input
                                type="password"
                                className="w-full bg-slate-900/50 border border-slate-600 rounded-xl px-4 py-3 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                                placeholder="••••••••"
                                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                            />
                        </div>

                        {error && <p className="text-red-400 text-sm font-medium animate-pulse">× {error}</p>}

                        <button
                            type="submit"
                            className="w-full bg-indigo-600 hover:bg-indigo-500 text-white font-bold py-3 rounded-xl shadow-lg shadow-indigo-500/20 transform active:scale-95 transition-all duration-200"
                        >
                            Sincronizar Acceso
                        </button>
                    </form>

                    <div className="mt-8 pt-6 border-t border-slate-700 text-center">
                        <p className="text-slate-400 text-sm">
                            ¿Sin autorización? <a href="#" className="text-indigo-400 hover:text-indigo-300 font-bold">Crear terminal</a>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
}
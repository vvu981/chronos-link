import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { registerRequest } from '../api/auth';

export default function RegisterPage() {
    const [formData, setFormData] = useState({
        email: '',
        username: '',
        password: ''
    });
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            await registerRequest(formData);
            // Tras el éxito, redirigimos al login para que el usuario estrene su acceso
            navigate('/login?registered=true');
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-[radial-gradient(ellipse_at_top_right,_var(--tw-gradient-stops))] from-slate-900 via-slate-900 to-indigo-950 flex items-center justify-center p-4">
            <div className="w-full max-w-md">
                <div className="text-center mb-8">
                    <h1 className="text-4xl font-extrabold text-white tracking-tighter italic">
                        CHRONOS<span className="text-emerald-500">INIT</span>
                    </h1>
                    <p className="text-slate-400 mt-2 font-medium">Creación de nueva identidad temporal</p>
                </div>

                <div className="bg-slate-800/40 backdrop-blur-2xl border border-slate-700/50 p-8 rounded-3xl shadow-2xl">
                    <form onSubmit={handleSubmit} className="space-y-5">
                        <div>
                            <label className="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Correo Electrónico</label>
                            <input
                                type="email"
                                required
                                className="w-full bg-slate-950/50 border border-slate-700 rounded-xl px-4 py-3 text-white focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all placeholder:text-slate-600"
                                placeholder="usuario@chronoslink.com"
                                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                            />
                        </div>

                        <div>
                            <label className="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Nombre de Usuario</label>
                            <input
                                type="text"
                                required
                                className="w-full bg-slate-950/50 border border-slate-700 rounded-xl px-4 py-3 text-white focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all placeholder:text-slate-600"
                                placeholder="operador_01"
                                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                            />
                        </div>

                        <div>
                            <label className="block text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">Contraseña Maestra</label>
                            <input
                                type="password"
                                required
                                className="w-full bg-slate-950/50 border border-slate-700 rounded-xl px-4 py-3 text-white focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all placeholder:text-slate-600"
                                placeholder="••••••••"
                                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                            />
                        </div>

                        {error && (
                            <div className="bg-red-500/10 border border-red-500/20 text-red-400 p-3 rounded-xl text-sm font-medium">
                                × {error}
                            </div>
                        )}

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full bg-emerald-600 hover:bg-emerald-500 disabled:bg-slate-700 text-white font-bold py-4 rounded-xl shadow-lg shadow-emerald-500/20 transform active:scale-95 transition-all duration-200"
                        >
                            {loading ? "Procesando ADN..." : "Registrar Terminal"}
                        </button>
                    </form>

                    <div className="mt-8 pt-6 border-t border-slate-700/50 text-center">
                        <p className="text-slate-400 text-sm font-medium">
                            ¿Ya tienes acceso? <Link to="/login" className="text-emerald-400 hover:text-emerald-300 font-bold transition-colors">Sincronizar aquí</Link>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
}
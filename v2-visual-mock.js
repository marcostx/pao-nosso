import {
  Bell,
  Calendar,
  Camera,
  CheckCircle2,
  Clock,
  Heart,
  Home,
  MapPin,
  PlusCircle,
  Store,
  Truck,
  User,
  X
} from 'lucide-react';
import React, { useState } from 'react';

const App = () => {
  const [activeTab, setActiveTab] = useState('home');
  const [donationStep, setDonationStep] = useState(1);
  const [deliveryMethod, setDeliveryMethod] = useState(null); // 'pickup' or 'dropoff'
  const [selectedDate, setSelectedDate] = useState('hoje'); // 'hoje' or 'amanha'
  const [selectedTime, setSelectedTime] = useState(null);

  // State for appointments/schedules
  const [appointments, setAppointments] = useState([
    {
      id: 1,
      item: "Cesta de Frutas",
      institution: "Sopa Solidária Centro",
      method: "Retirada",
      date: "Hoje",
      time: "14:30",
      status: "Confirmado",
      address: "Rua das Flores, 123"
    },
    {
      id: 2,
      item: "5kg de Arroz",
      institution: "Mãos que Alimentam",
      method: "Entrega",
      date: "Amanhã",
      time: "10:00",
      status: "Pendente",
      address: "Ponto de Coleta B"
    }
  ]);

  const entities = [
    { id: 1, name: "Sopa Solidária Centro", dist: "0.8km", type: "ONG" },
    { id: 2, name: "Mãos que Alimentam", dist: "1.2km", type: "Igreja" },
    { id: 3, name: "Banco de Alimentos Municipal", dist: "2.5km", type: "Governo" },
  ];

  const timeSlots = {
    hoje: ["14:00", "15:30", "17:00"],
    amanha: ["09:00", "10:30", "14:00", "16:00"]
  };

  const HomeView = () => (
    <div className="flex flex-col space-y-6 pb-24">
      <header className="bg-emerald-600 text-white p-6 rounded-b-3xl shadow-lg relative overflow-hidden">
        <div className="relative z-10">
          <h1 className="text-2xl font-bold">Olá, Maria! 👋</h1>
          <p className="opacity-90">Vamos fazer o bem hoje?</p>
          <div className="mt-6 bg-white/20 p-4 rounded-2xl backdrop-blur-md flex justify-between items-center">
            <div>
              <p className="text-xs uppercase tracking-wider font-semibold">Seu Impacto</p>
              <p className="text-xl font-bold">12 Refeições Salvas</p>
            </div>
            <Heart className="fill-white text-white" size={32} />
          </div>
        </div>
        <div className="absolute top-[-20%] right-[-10%] w-40 h-40 bg-white/10 rounded-full blur-3xl"></div>
      </header>

      <section className="px-6 grid grid-cols-2 gap-4">
        <button
          onClick={() => { setActiveTab('donate'); setDonationStep(1); }}
          className="bg-orange-100 p-4 rounded-2xl flex flex-col items-center text-orange-700 font-medium border border-orange-200 transition-transform active:scale-95"
        >
          <div className="bg-orange-500 p-2 rounded-full text-white mb-2">
            <PlusCircle size={24} />
          </div>
          Doar Alimento
        </button>
        <button
          onClick={() => setActiveTab('appointments')}
          className="bg-emerald-100 p-4 rounded-2xl flex flex-col items-center text-emerald-700 font-medium border border-emerald-200 transition-transform active:scale-95"
        >
          <div className="bg-emerald-500 p-2 rounded-full text-white mb-2">
            <Calendar size={24} />
          </div>
          Agendamentos
        </button>
      </section>

      <section className="px-6">
        <h2 className="text-lg font-bold mb-4 flex items-center gap-2">
          <Clock size={20} className="text-emerald-600" />
          Próximas Coletas
        </h2>
        <div className="space-y-3">
          {appointments.filter(a => a.status === 'Confirmado').map((app) => (
            <div key={app.id} className="flex items-center p-4 bg-white rounded-xl shadow-sm border-l-4 border-emerald-500">
              <div className="flex-1">
                <p className="font-bold text-gray-800">{app.item}</p>
                <p className="text-xs text-gray-500">{app.institution} • {app.date}, {app.time}</p>
              </div>
              <div className="bg-emerald-50 text-emerald-700 p-2 rounded-lg">
                <CheckCircle2 size={20} />
              </div>
            </div>
          ))}
        </div>
      </section>
    </div>
  );

  const AppointmentsView = () => (
    <div className="flex flex-col h-full bg-gray-50 pb-24">
      <header className="p-6 bg-white border-b">
        <h2 className="text-xl font-bold text-gray-800">Meus Agendamentos</h2>
        <p className="text-sm text-gray-500">Acompanhe o status das suas doações</p>
      </header>

      <div className="p-4 space-y-4 overflow-y-auto">
        {appointments.map(app => (
          <div key={app.id} className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100">
            <div className="flex justify-between items-start mb-3">
              <div>
                <h4 className="font-bold text-gray-800">{app.item}</h4>
                <p className="text-xs text-gray-500 flex items-center gap-1 mt-1">
                  <MapPin size={12} /> {app.institution}
                </p>
              </div>
              <span className={`text-[10px] font-bold px-2 py-1 rounded-full uppercase tracking-tighter ${app.status === 'Confirmado' ? 'bg-emerald-100 text-emerald-700' :
                  app.status === 'Pendente' ? 'bg-orange-100 text-orange-700' : 'bg-red-100 text-red-700'
                }`}>
                {app.status}
              </span>
            </div>

            <div className="grid grid-cols-2 gap-2 bg-gray-50 p-3 rounded-xl text-xs">
              <div className="flex items-center gap-2">
                <Calendar size={14} className="text-gray-400" />
                <span className="text-gray-600 font-medium">{app.date}</span>
              </div>
              <div className="flex items-center gap-2">
                <Clock size={14} className="text-gray-400" />
                <span className="text-gray-600 font-medium">{app.time}</span>
              </div>
              <div className="flex items-center gap-2 col-span-2 mt-1">
                {app.method === 'Retirada' ? <Truck size={14} className="text-gray-400" /> : <Store size={14} className="text-gray-400" />}
                <span className="text-gray-600 truncate">{app.address}</span>
              </div>
            </div>

            {app.status === 'Pendente' && (
              <div className="mt-3 flex gap-2">
                <button className="flex-1 text-xs text-red-500 font-bold p-2 hover:bg-red-50 rounded-lg transition-colors">Cancelar</button>
                <div className="flex-1 flex items-center justify-end text-[10px] text-gray-400 italic">Aguardando instituição...</div>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );

  const DonateView = () => (
    <div className="flex flex-col h-full bg-white pb-24">
      <header className="p-6 border-b flex justify-between items-center">
        <div>
          <h2 className="text-xl font-bold">Nova Doação</h2>
          <p className="text-xs text-gray-400">Passo {donationStep} de 3</p>
        </div>
        <button onClick={() => setActiveTab('home')} className="p-2 text-gray-400 bg-gray-50 rounded-full"><X size={20} /></button>
      </header>

      <div className="flex-1 p-6 overflow-y-auto">
        {donationStep === 1 && (
          <div className="space-y-6 animate-in fade-in slide-in-from-bottom-4">
            <div className="text-center py-10 border-2 border-dashed border-gray-200 rounded-3xl bg-gray-50 group active:bg-gray-100 transition-colors">
              <div className="bg-white w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-3 shadow-sm">
                <Camera size={32} className="text-emerald-600" />
              </div>
              <p className="text-gray-500 font-medium">Tire uma foto do alimento</p>
              <p className="text-[10px] text-gray-400 mt-1 uppercase font-bold tracking-widest">Opcional</p>
            </div>
            <div>
              <label className="block text-sm font-bold text-gray-700 mb-2">O que você está doando?</label>
              <input type="text" placeholder="Ex: 5kg de arroz, Cesta de laranjas..." className="w-full p-4 bg-gray-100 rounded-xl focus:ring-2 focus:ring-emerald-500 outline-none border-none transition-all" />
            </div>
            <div>
              <label className="block text-sm font-bold text-gray-700 mb-2">Categoria</label>
              <div className="grid grid-cols-2 gap-2">
                {['Perecível', 'Não-perecível', 'Refeição Pronta', 'Hortifruti'].map(cat => (
                  <button key={cat} className="p-3 border-2 border-gray-100 rounded-xl hover:border-emerald-500 hover:bg-emerald-50 text-sm font-bold text-gray-600 transition-all">
                    {cat}
                  </button>
                ))}
              </div>
            </div>
            <button onClick={() => setDonationStep(2)} className="w-full bg-emerald-600 text-white py-4 rounded-xl font-bold shadow-lg shadow-emerald-200 active:scale-95 transition-transform">Próximo</button>
          </div>
        )}

        {donationStep === 2 && (
          <div className="space-y-6 animate-in fade-in slide-in-from-right-4">
            <h3 className="font-bold text-gray-800">Logística de Entrega</h3>

            <div className="grid grid-cols-2 gap-3">
              <button
                onClick={() => setDeliveryMethod('dropoff')}
                className={`p-4 rounded-2xl border-2 flex flex-col items-center gap-2 transition-all ${deliveryMethod === 'dropoff' ? 'border-emerald-500 bg-emerald-50' : 'border-gray-100'}`}
              >
                <Store size={24} className={deliveryMethod === 'dropoff' ? 'text-emerald-600' : 'text-gray-400'} />
                <p className="text-xs font-bold">Eu entrego</p>
              </button>

              <button
                onClick={() => setDeliveryMethod('pickup')}
                className={`p-4 rounded-2xl border-2 flex flex-col items-center gap-2 transition-all ${deliveryMethod === 'pickup' ? 'border-emerald-500 bg-emerald-50' : 'border-gray-100'}`}
              >
                <Truck size={24} className={deliveryMethod === 'pickup' ? 'text-emerald-600' : 'text-gray-400'} />
                <p className="text-xs font-bold">Solicitar Coleta</p>
              </button>
            </div>

            {deliveryMethod && (
              <div className="space-y-6 animate-in fade-in zoom-in-95">
                {deliveryMethod === 'dropoff' ? (
                  <div>
                    <label className="block text-sm font-bold text-gray-700 mb-3">Escolha o Ponto de Coleta</label>
                    <div className="space-y-2">
                      {entities.slice(0, 2).map(e => (
                        <div key={e.id} className="p-4 border-2 rounded-2xl flex justify-between items-center bg-white border-gray-100 hover:border-emerald-200 active:bg-emerald-50 transition-colors cursor-pointer">
                          <div>
                            <p className="text-sm font-bold text-gray-800">{e.name}</p>
                            <p className="text-[10px] text-gray-400 uppercase font-bold tracking-tighter">{e.dist} de você</p>
                          </div>
                          <input type="radio" name="entity" className="accent-emerald-600 w-5 h-5" />
                        </div>
                      ))}
                    </div>
                  </div>
                ) : (
                  <div>
                    <label className="block text-sm font-bold text-gray-700 mb-2">Endereço para Retirada</label>
                    <div className="flex gap-2">
                      <input type="text" defaultValue="Rua das Flores, 123" className="flex-1 p-4 bg-gray-100 rounded-xl outline-none text-sm border-none" />
                      <button className="bg-gray-100 p-4 rounded-xl text-emerald-600"><MapPin size={20} /></button>
                    </div>
                  </div>
                )}

                <div>
                  <div className="flex justify-between items-center mb-3">
                    <label className="text-sm font-bold text-gray-700">Quando?</label>
                    <div className="flex bg-gray-100 p-1 rounded-lg">
                      <button
                        onClick={() => { setSelectedDate('hoje'); setSelectedTime(null); }}
                        className={`px-3 py-1 text-[10px] font-bold rounded-md transition-all ${selectedDate === 'hoje' ? 'bg-white shadow-sm text-emerald-600' : 'text-gray-400'}`}
                      >HOJE</button>
                      <button
                        onClick={() => { setSelectedDate('amanha'); setSelectedTime(null); }}
                        className={`px-3 py-1 text-[10px] font-bold rounded-md transition-all ${selectedDate === 'amanha' ? 'bg-white shadow-sm text-emerald-600' : 'text-gray-400'}`}
                      >AMANHÃ</button>
                    </div>
                  </div>

                  <div className="grid grid-cols-3 gap-2">
                    {timeSlots[selectedDate].map(time => (
                      <button
                        key={time}
                        onClick={() => setSelectedTime(time)}
                        className={`p-3 rounded-xl border-2 text-xs font-bold transition-all ${selectedTime === time ? 'border-emerald-500 bg-emerald-50 text-emerald-700' : 'border-gray-100 text-gray-500'}`}
                      >
                        {time}
                      </button>
                    ))}
                  </div>
                </div>
              </div>
            )}

            <div className="flex gap-4 pt-4">
              <button onClick={() => setDonationStep(1)} className="flex-1 border-2 border-gray-100 py-4 rounded-xl font-bold text-gray-400 active:bg-gray-50 transition-colors">Voltar</button>
              <button
                onClick={() => setDonationStep(3)}
                disabled={!deliveryMethod || !selectedTime}
                className={`flex-[2] py-4 rounded-xl font-bold text-white shadow-lg transition-all ${(!deliveryMethod || !selectedTime) ? 'bg-gray-300 shadow-none' : 'bg-emerald-600 shadow-emerald-200 active:scale-95'}`}
              >
                Agendar Doação
              </button>
            </div>
          </div>
        )}

        {donationStep === 3 && (
          <div className="text-center py-10 flex flex-col items-center animate-in zoom-in-95">
            <div className="relative mb-6">
              <div className="w-24 h-24 bg-emerald-100 text-emerald-600 rounded-full flex items-center justify-center animate-bounce">
                <Bell size={48} />
              </div>
              <div className="absolute -top-1 -right-1 w-8 h-8 bg-orange-500 border-4 border-white rounded-full flex items-center justify-center text-white font-bold text-xs">!</div>
            </div>
            <h2 className="text-2xl font-bold text-gray-800 mb-2">Pedido Enviado!</h2>
            <p className="text-gray-500 mb-8 px-6 leading-relaxed">
              A instituição recebeu o seu agendamento para <strong>{selectedDate === 'hoje' ? 'Hoje' : 'Amanhã'} às {selectedTime}</strong>.
              Fique atento à aba <strong>Agendamentos</strong> para a confirmação.
            </p>
            <div className="w-full space-y-3">
              <button
                onClick={() => { setActiveTab('appointments'); setDonationStep(1); setDeliveryMethod(null); setSelectedTime(null); }}
                className="w-full bg-emerald-600 text-white py-4 rounded-xl font-bold shadow-lg shadow-emerald-200"
              >
                Ver meus agendamentos
              </button>
              <button
                onClick={() => { setActiveTab('home'); setDonationStep(1); setDeliveryMethod(null); setSelectedTime(null); }}
                className="w-full py-4 text-emerald-600 font-bold hover:bg-emerald-50 rounded-xl transition-colors"
              >
                Voltar ao Início
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );

  return (
    <div className="max-w-md mx-auto h-screen bg-white flex flex-col font-sans overflow-hidden shadow-2xl relative">
      {/* View Content */}
      <main className="flex-1 overflow-y-auto">
        {activeTab === 'home' && <HomeView />}
        {activeTab === 'donate' && <DonateView />}
        {activeTab === 'map' && (
          <div className="h-full flex flex-col items-center justify-center p-8 text-center bg-gray-50">
            <MapPin size={64} className="text-emerald-200 mb-4" />
            <h3 className="font-bold text-gray-800">Mapa em desenvolvimento</h3>
            <p className="text-sm text-gray-500">Em breve você poderá ver os pontos de coleta em tempo real aqui.</p>
          </div>
        )}
        {activeTab === 'appointments' && <AppointmentsView />}
        {activeTab === 'profile' && (
          <div className="p-8 text-center flex flex-col items-center justify-center h-full bg-white">
            <div className="w-24 h-24 bg-gray-100 rounded-full mb-4 border-4 border-emerald-500 p-1 shadow-inner">
              <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Maria" alt="Avatar" className="rounded-full" />
            </div>
            <h2 className="text-2xl font-bold text-gray-800">Maria Silva</h2>
            <p className="text-gray-500 mb-6">Doadora Solidária desde 2023</p>
            <div className="w-full grid grid-cols-2 gap-4">
              <div className="bg-emerald-50 p-4 rounded-2xl border border-emerald-100">
                <p className="text-3xl font-bold text-emerald-600">12</p>
                <p className="text-[10px] text-emerald-500 uppercase font-bold tracking-wider">Doações</p>
              </div>
              <div className="bg-orange-50 p-4 rounded-2xl border border-orange-100">
                <p className="text-3xl font-bold text-orange-600">54kg</p>
                <p className="text-[10px] text-orange-500 uppercase font-bold tracking-wider">Peso Total</p>
              </div>
            </div>
            <button className="mt-8 w-full border-2 border-gray-100 p-4 rounded-2xl font-bold text-gray-600 hover:bg-gray-50">Editar Perfil</button>
            <button className="mt-4 w-full text-red-500 font-bold p-2">Sair da conta</button>
          </div>
        )}
      </main>

      {/* Navigation Bar */}
      <nav className="absolute bottom-0 left-0 right-0 bg-white/90 backdrop-blur-md border-t border-gray-100 px-6 py-4 flex justify-between items-center rounded-t-3xl shadow-[0_-4px_24px_rgba(0,0,0,0.06)] z-50">
        <button
          onClick={() => setActiveTab('home')}
          className={`flex flex-col items-center transition-all ${activeTab === 'home' ? 'text-emerald-600 scale-110' : 'text-gray-400'}`}
        >
          <Home size={22} strokeWidth={activeTab === 'home' ? 2.5 : 2} />
          <span className="text-[9px] mt-1 font-black uppercase tracking-tighter">Início</span>
        </button>
        <button
          onClick={() => setActiveTab('appointments')}
          className={`flex flex-col items-center transition-all ${activeTab === 'appointments' ? 'text-emerald-600 scale-110' : 'text-gray-400'}`}
        >
          <div className="relative">
            <Calendar size={22} strokeWidth={activeTab === 'appointments' ? 2.5 : 2} />
            {appointments.some(a => a.status === 'Pendente') && <div className="absolute -top-1 -right-1 w-2.5 h-2.5 bg-orange-500 rounded-full border-2 border-white" />}
          </div>
          <span className="text-[9px] mt-1 font-black uppercase tracking-tighter">Agenda</span>
        </button>

        <button
          onClick={() => { setActiveTab('donate'); setDonationStep(1); }}
          className="relative -top-10 bg-emerald-600 text-white p-4 rounded-2xl shadow-xl shadow-emerald-200 transition-all hover:scale-110 active:scale-95 group"
        >
          <PlusCircle size={28} className="group-hover:rotate-90 transition-transform duration-300" />
        </button>

        <button
          onClick={() => setActiveTab('map')}
          className={`flex flex-col items-center transition-all ${activeTab === 'map' ? 'text-emerald-600 scale-110' : 'text-gray-400'}`}
        >
          <MapPin size={22} strokeWidth={activeTab === 'map' ? 2.5 : 2} />
          <span className="text-[9px] mt-1 font-black uppercase tracking-tighter">Mapa</span>
        </button>
        <button
          onClick={() => setActiveTab('profile')}
          className={`flex flex-col items-center transition-all ${activeTab === 'profile' ? 'text-emerald-600 scale-110' : 'text-gray-400'}`}
        >
          <User size={22} strokeWidth={activeTab === 'profile' ? 2.5 : 2} />
          <span className="text-[9px] mt-1 font-black uppercase tracking-tighter">Perfil</span>
        </button>
      </nav>

      <style>{`
        @keyframes fade-in { from { opacity: 0; } to { opacity: 1; } }
        @keyframes slide-in-from-bottom { from { transform: translateY(12px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }
        @keyframes slide-in-from-right { from { transform: translateX(20px); opacity: 0; } to { transform: translateX(0); opacity: 1; } }
        @keyframes zoom-in { from { transform: scale(0.95); opacity: 0; } to { transform: scale(1); opacity: 1; } }
        .animate-in { animation-duration: 400ms; animation-fill-mode: both; animation-timing-function: cubic-bezier(0.16, 1, 0.3, 1); }
        .fade-in { animation-name: fade-in; }
        .slide-in-from-bottom-4 { animation-name: slide-in-from-bottom; }
        .slide-in-from-right-4 { animation-name: slide-in-from-right; }
        .zoom-in-95 { animation-name: zoom-in; }
      `}</style>
    </div>
  );
};

export default App;
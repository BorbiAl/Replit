
import { useAuth } from "@/hooks/auth";
import { Sparkles } from "lucide-react";

export default function UserStats() {
  const { user } = useAuth();
  
  if (!user) return null;
  
  const xpPercentage = user.xp % 100;
  const strokeDasharray = 100;
  const strokeDashoffset = strokeDasharray - (strokeDasharray * xpPercentage) / 100;
  
  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div className="stat-card group">
        <div className="relative overflow-hidden rounded-xl bg-gradient-to-br from-amber-100 to-amber-50 p-6 shadow-lg transition-all duration-300 hover:shadow-2xl hover:scale-[1.02]">
          <div className="absolute -right-4 -top-4 h-20 w-20 rounded-full bg-amber-500/10 group-hover:scale-150 transition-transform duration-500" />
          <div className="relative">
            <svg className="w-8 h-8 text-amber-500 streak-flame mb-4" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M12.395 2.553a1 1 0 00-1.45-.385c-.345.23-.614.558-.822.88-.214.33-.403.713-.57 1.116-.334.804-.614 1.768-.84 2.734a31.365 31.365 0 00-.613 3.58 2.64 2.64 0 01-.945-1.067c-.328-.68-.398-1.534-.398-2.654A1 1 0 005.05 6.05 6.981 6.981 0 003 11a7 7 0 1011.95-4.95c-.592-.591-.98-.985-1.348-1.467-.363-.476-.724-1.063-1.207-2.03zM12.12 15.12A3 3 0 017 13s.879.5 2.5.5c0-1 .5-4 1.25-4.5.5 1 .786 1.293 1.371 1.879A2.99 2.99 0 0113 13a2.99 2.99 0 01-.879 2.121z" clipRule="evenodd" />
            </svg>
            <h3 className="text-lg font-semibold text-amber-900">Daily Streak</h3>
            <p className="mt-2 text-3xl font-bold text-amber-600">{user.streak}</p>
            <p className="text-sm text-amber-700 mt-1">Keep it up!</p>
          </div>
        </div>
      </div>

      <div className="stat-card group">
        <div className="relative overflow-hidden rounded-xl bg-gradient-to-br from-primary/10 to-primary/5 p-6 shadow-lg transition-all duration-300 hover:shadow-2xl hover:scale-[1.02]">
          <div className="absolute -right-4 -top-4 h-20 w-20 rounded-full bg-primary/10 group-hover:scale-150 transition-transform duration-500" />
          <div className="relative">
            <div className="relative w-8 h-8 mb-4">
              <svg className="progress-ring" width="32" height="32">
                <circle
                  stroke="currentColor"
                  strokeWidth="4"
                  fill="transparent"
                  r="14"
                  cx="16"
                  cy="16"
                  className="text-primary/20"
                />
                <circle
                  stroke="currentColor"
                  strokeWidth="4"
                  fill="transparent"
                  r="14"
                  cx="16"
                  cy="16"
                  strokeDasharray={strokeDasharray}
                  strokeDashoffset={strokeDashoffset}
                  className="text-primary transition-all duration-1000 ease-out"
                />
              </svg>
            </div>
            <h3 className="text-lg font-semibold text-primary-900">Experience</h3>
            <p className="mt-2 text-3xl font-bold text-primary">{user.xp}</p>
            <p className="text-sm text-primary-700 mt-1">Level {user.level}</p>
          </div>
        </div>
      </div>

      <div className="stat-card group">
        <div className="relative overflow-hidden rounded-xl bg-gradient-to-br from-purple-100 to-purple-50 p-6 shadow-lg transition-all duration-300 hover:shadow-2xl hover:scale-[1.02]">
          <div className="absolute -right-4 -top-4 h-20 w-20 rounded-full bg-purple-500/10 group-hover:scale-150 transition-transform duration-500" />
          <div className="relative">
            <Sparkles className="w-8 h-8 text-purple-500 mb-4" />
            <h3 className="text-lg font-semibold text-purple-900">Achievements</h3>
            <p className="mt-2 text-3xl font-bold text-purple-600">12</p>
            <p className="text-sm text-purple-700 mt-1">3 new this week</p>
          </div>
        </div>
      </div>
    </div>
  );
}

import { useAuth } from "@/hooks/use-auth";

export default function UserStats() {
  const { user } = useAuth();
  
  if (!user) return null;
  
  // Calculate XP percentage for progress ring
  const xpPercentage = user.xp % 100;
  const strokeDasharray = 100;
  const strokeDashoffset = strokeDasharray - (strokeDasharray * xpPercentage) / 100;
  
  return (
    <>
      {/* Streak Component */}
      <div className="hidden sm:flex items-center space-x-2">
        <div className="relative">
          <svg className="w-6 h-6 text-accent streak-flame" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M12.395 2.553a1 1 0 00-1.45-.385c-.345.23-.614.558-.822.88-.214.33-.403.713-.57 1.116-.334.804-.614 1.768-.84 2.734a31.365 31.365 0 00-.613 3.58 2.64 2.64 0 01-.945-1.067c-.328-.68-.398-1.534-.398-2.654A1 1 0 005.05 6.05 6.981 6.981 0 003 11a7 7 0 1011.95-4.95c-.592-.591-.98-.985-1.348-1.467-.363-.476-.724-1.063-1.207-2.03zM12.12 15.12A3 3 0 017 13s.879.5 2.5.5c0-1 .5-4 1.25-4.5.5 1 .786 1.293 1.371 1.879A2.99 2.99 0 0113 13a2.99 2.99 0 01-.879 2.121z" clipRule="evenodd"></path>
          </svg>
          <span className="absolute -bottom-1 -right-1 bg-accent text-xs font-bold rounded-full w-4 h-4 flex items-center justify-center">{user.streak}</span>
        </div>
        <span className="text-sm font-semibold">{user.streak} Day Streak</span>
      </div>
      
      {/* XP Component */}
      <div className="hidden sm:flex items-center space-x-2">
        <div className="relative flex items-center justify-center">
          <svg className="w-8 h-8" viewBox="0 0 36 36">
            <circle cx="18" cy="18" r="16" fill="none" stroke="#ECECEC" strokeWidth="3"></circle>
            <circle className="progress-ring" cx="18" cy="18" r="16" fill="none" stroke="#FFC800" strokeWidth="3" strokeDasharray={strokeDasharray} strokeDashoffset={strokeDashoffset}></circle>
            <text x="18" y="18" textAnchor="middle" dominantBaseline="middle" fill="#3C3C3C" fontSize="10" fontWeight="bold">{xpPercentage}%</text>
          </svg>
        </div>
        <span className="text-sm font-semibold">{user.xp} XP</span>
      </div>
    </>
  );
}

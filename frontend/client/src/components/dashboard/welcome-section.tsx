interface WelcomeSectionProps {
  firstName: string;
  testsCount: number;
}

export default function WelcomeSection({ firstName, testsCount }: WelcomeSectionProps) {
  return (
    <div className="mb-8">
      <h1 className="text-2xl font-bold text-neutral-900 mb-2">
        Welcome back, {firstName}!
      </h1>
      <p className="text-neutral-700">
        You have <span className="font-semibold text-primary">{testsCount} {testsCount === 1 ? 'test' : 'tests'}</span> scheduled for this week. 
        {testsCount > 0 ? ' Keep up the good work!' : ' Create your first test to get started!'}
      </p>
    </div>
  );
}

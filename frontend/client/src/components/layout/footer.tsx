export default function Footer() {
  return (
    <footer className="bg-white border-t border-neutral-200 py-4">
      <div className="container mx-auto px-4 text-center text-sm text-neutral-700">
        <p>© {new Date().getFullYear()} StudyQuest. All rights reserved.</p>
      </div>
    </footer>
  );
}

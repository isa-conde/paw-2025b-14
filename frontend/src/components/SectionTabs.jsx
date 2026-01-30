const SectionTabs = ({ sections, activeSection, onChange }) => (
    <div className="tabs">
        {sections.map((section) => (
            <button
                key={section.id}
                type="button"
                className={`tab ${activeSection === section.id ? 'active' : ''}`}
                onClick={() => onChange(section.id)}
            >
                {section.label}
            </button>
        ))}
    </div>
);

export default SectionTabs;
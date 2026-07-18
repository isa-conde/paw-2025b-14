const SectionTabs = ({
    sections,
    activeSection,
    onChange,
    className = 'tabs',
    tabClassName = 'tab',
    activeTabClassName = 'active',
}) => (
    <div className={className}>
        {sections.map((section) => (
            <button
                key={section.id}
                type="button"
                className={`${tabClassName} ${activeSection === section.id ? activeTabClassName : ''}`}
                onClick={() => onChange(section.id)}
                aria-pressed={activeSection === section.id}
            >
                {section.label}
            </button>
        ))}
    </div>
);

export default SectionTabs;

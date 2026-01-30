const InfoCard = ({ title, value, subtitle }) => (
    <div className="info-card">
        <p className="info-card-title">{title}</p>
        <p className="info-card-value">{value}</p>
        {subtitle ? <p className="info-card-subtitle">{subtitle}</p> : null}
    </div>
);

export default InfoCard;
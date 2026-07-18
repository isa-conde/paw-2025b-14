import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Button } from '../Button.jsx';
import styles from '../../styles/pages/TournamentPage.module.css';

const isPdfFile = (file) => (
    file?.type === 'application/pdf'
    || /\.pdf$/i.test(file?.name ?? '')
);

const RulesUploadForm = ({
    hasRules,
    uploadState,
    onUploadRules,
}) => {
    const { t } = useTranslation();
    const inputRef = useRef(null);
    const [selectedFile, setSelectedFile] = useState(null);
    const [fileError, setFileError] = useState(null);
    const isUploading = uploadState?.status === 'uploading';

    useEffect(() => {
        if (uploadState?.status === 'success') {
            setSelectedFile(null);
            setFileError(null);
            if (inputRef.current) {
                inputRef.current.value = '';
            }
        }
    }, [uploadState?.status]);

    const handleFileChange = (event) => {
        const [file] = event.target.files ?? [];
        setSelectedFile(file ?? null);
        setFileError(file && !isPdfFile(file) ? t('tournamentDetail.rules.invalidType') : null);
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        if (!selectedFile || !isPdfFile(selectedFile)) {
            setFileError(t('tournamentDetail.rules.invalidType'));
            return;
        }
        setFileError(null);
        onUploadRules(selectedFile);
    };

    return (
        <form className={styles.rulesUpload} onSubmit={handleSubmit}>
            <label className={styles.fileLabel} htmlFor="tournament-rules-file">
                {t('tournamentDetail.rules.selectFile')}
            </label>
            <input
                ref={inputRef}
                id="tournament-rules-file"
                className={styles.fileInput}
                type="file"
                accept="application/pdf,.pdf"
                onChange={handleFileChange}
                disabled={isUploading}
            />
            <Button
                type="submit"
                text={isUploading
                    ? t('tournamentDetail.rules.uploading')
                    : t(hasRules ? 'tournamentDetail.rules.replace' : 'tournamentDetail.rules.upload')}
                disabled={!selectedFile || isUploading}
            />
            {fileError && (
                <p className={styles.formError} role="alert">{fileError}</p>
            )}
            {uploadState?.status === 'error' && (
                <p className={styles.formError} role="alert">{uploadState.error}</p>
            )}
            {uploadState?.status === 'success' && (
                <p className={styles.formSuccess} role="status">{t('tournamentDetail.rules.uploadSuccess')}</p>
            )}
        </form>
    );
};

export const TournamentRules = ({
    rulesState,
    isOwner = false,
    uploadState = { status: 'idle', error: null },
    onUploadRules,
}) => {
    const { t } = useTranslation();
    const uploadForm = isOwner && onUploadRules ? (
        <RulesUploadForm
            hasRules={rulesState.status === 'available'}
            uploadState={uploadState}
            onUploadRules={onUploadRules}
        />
    ) : null;

    if (rulesState.status === 'loading' || rulesState.status === 'idle') {
        return (
            <section className={styles.section}>
                <h2 className={styles.sectionTitle}>{t('tournament.rules')}</h2>
                <p className={styles.empty}>{t('tournamentDetail.rules.loading')}</p>
                {uploadForm}
            </section>
        );
    }

    if (rulesState.status === 'not-found') {
        return (
            <section className={styles.section}>
                <h2 className={styles.sectionTitle}>{t('tournament.rules')}</h2>
                <p className={styles.empty}>{t('tournamentDetail.rules.empty')}</p>
                {uploadForm}
            </section>
        );
    }

    if (rulesState.status === 'error') {
        return (
            <section className={styles.section}>
                <h2 className={styles.sectionTitle}>{t('tournament.rules')}</h2>
                <div className={`${styles.panel} ${styles.alert}`} role="alert">
                    {t('tournamentDetail.rules.error')}
                </div>
                {uploadForm}
            </section>
        );
    }

    return (
        <section className={styles.section}>
            <h2 className={styles.sectionTitle}>{t('tournament.rules')}</h2>
            <div className={styles.panel}>
                <p className={styles.muted}>{t('tournament.rules.text')}</p>
                <div className={styles.rulesLinks}>
                    <a
                        className={styles.linkButton}
                        href={rulesState.url}
                        target="_blank"
                        rel="noreferrer"
                    >
                        {t('tournamentDetail.rules.open')}
                    </a>
                    <a
                        className={styles.linkButton}
                        href={rulesState.url}
                        download="tournament-rules.pdf"
                    >
                        {t('tournament.rules.download')}
                    </a>
                </div>
                <iframe
                    className={styles.rulesFrame}
                    title={t('tournamentDetail.rules.previewTitle')}
                    src={rulesState.url}
                />
            </div>
            {uploadForm}
        </section>
    );
};

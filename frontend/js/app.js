const API_BASE_URL = 'http://localhost:8080/api';

// Handle drag and drop and file selection
document.addEventListener('DOMContentLoaded', () => {
    const uploadArea = document.getElementById('upload-area');
    const fileInput = document.getElementById('file-input');
    
    if (uploadArea && fileInput) {
        uploadArea.addEventListener('dragover', (e) => {
            e.preventDefault();
            uploadArea.classList.add('drag-over');
        });

        uploadArea.addEventListener('dragleave', () => {
            uploadArea.classList.remove('drag-over');
        });

        uploadArea.addEventListener('drop', (e) => {
            e.preventDefault();
            uploadArea.classList.remove('drag-over');
            if (e.dataTransfer.files.length) {
                handleFileUpload(e.dataTransfer.files[0]);
            }
        });

        fileInput.addEventListener('change', (e) => {
            if (e.target.files.length) {
                handleFileUpload(e.target.files[0]);
            }
        });

        loadDocuments();
    }
});

async function handleFileUpload(file) {
    const statusDiv = document.getElementById('upload-status');
    statusDiv.classList.remove('hidden', 'status-error', 'status-success');
    statusDiv.className = 'status-message';
    statusDiv.innerText = 'Uploading and processing document...';

    const formData = new FormData();
    formData.append('file', file);

    try {
        const response = await fetch(`${API_BASE_URL}/documents/upload`, {
            method: 'POST',
            body: formData
        });

        const data = await response.json();

        if (response.ok) {
            statusDiv.classList.add('status-success');
            statusDiv.innerText = 'Upload successful!';
            setTimeout(() => {
                window.location.href = `result.html?id=${data.id}`;
            }, 1000);
        } else {
            throw new Error(data.error || 'Upload failed');
        }
    } catch (error) {
        statusDiv.classList.add('status-error');
        statusDiv.innerText = error.message;
    }
}

async function loadDocuments() {
    const tbody = document.getElementById('documents-body');
    if (!tbody) return;

    try {
        const response = await fetch(`${API_BASE_URL}/documents`);
        const documents = await response.json();

        tbody.innerHTML = '';
        
        if (documents.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align: center;">No documents found</td></tr>';
            return;
        }

        documents.forEach(doc => {
            const date = new Date(doc.createdAt).toLocaleDateString();
            const row = `
                <tr>
                    <td>${doc.fileName}</td>
                    <td>${doc.fileType.toUpperCase()}</td>
                    <td>${date}</td>
                    <td>
                        <a href="result.html?id=${doc.id}" class="btn action-btn" style="padding: 0.25rem 0.5rem; font-size: 0.8rem;">View</a>
                    </td>
                </tr>
            `;
            tbody.innerHTML += row;
        });
    } catch (error) {
        console.error('Failed to load documents:', error);
    }
}

async function fetchDocument(id) {
    try {
        const response = await fetch(`${API_BASE_URL}/documents/${id}`);
        if (!response.ok) throw new Error('Document not found');
        const doc = await response.json();
        document.getElementById('doc-title').innerText = `Document: ${doc.fileName}`;
    } catch (error) {
        alert(error.message);
        window.location.href = 'index.html';
    }
}

async function requestAi(actionType) {
    const docId = new URLSearchParams(window.location.search).get('id');
    const loading = document.getElementById('ai-loading');
    const errorDiv = document.getElementById('ai-error');
    const resultArea = document.getElementById('result-area');
    const resultContent = document.getElementById('result-content');
    const resultTitle = document.getElementById('result-title');
    
    // Disable all action buttons
    const buttons = document.querySelectorAll('.action-btn');
    buttons.forEach(btn => btn.disabled = true);

    loading.classList.remove('hidden');
    errorDiv.classList.add('hidden');
    resultArea.classList.add('hidden');

    const titles = {
        'summarize': 'AI Summary',
        'key-points': 'Key Points',
        'explain': 'Simple Explanation',
        'topics': 'Important Topics'
    };

    try {
        const response = await fetch(`${API_BASE_URL}/ai/${actionType}/${docId}`, {
            method: 'POST'
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'AI Request failed');
        }

        resultTitle.innerText = titles[actionType];
        resultContent.innerText = data.response;
        resultArea.classList.remove('hidden');
    } catch (error) {
        errorDiv.innerText = error.message;
        errorDiv.classList.remove('hidden');
    } finally {
        loading.classList.add('hidden');
        buttons.forEach(btn => btn.disabled = false);
    }
}

function copyResult() {
    const content = document.getElementById('result-content').innerText;
    navigator.clipboard.writeText(content)
        .then(() => alert('Result copied to clipboard!'))
        .catch(err => console.error('Failed to copy text: ', err));
}

function downloadResult() {
    const title = document.getElementById('result-title').innerText.replace(/\s+/g, '_').toLowerCase();
    const content = document.getElementById('result-content').innerText;
    const blob = new Blob([content], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${title}.txt`;
    a.click();
    window.URL.revokeObjectURL(url);
}

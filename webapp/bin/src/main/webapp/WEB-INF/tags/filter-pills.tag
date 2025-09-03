<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<div class="filter-pills-container">
    <div class="filter-titles-row">
        <button class="filter-title" data-target="game-filters">
            <span>Game</span>
            <span class="filter-arrow">▼</span>
        </button>
        <button class="filter-title" data-target="format-filters">
            <span>Format</span>
            <span class="filter-arrow">▼</span>
        </button>
        <button class="filter-title" data-target="level-filters">
            <span>Level</span>
            <span class="filter-arrow">▼</span>
        </button>
        <button class="filter-title" data-target="region-filters">
            <span>Region</span>
            <span class="filter-arrow">▼</span>
        </button>
    </div>
    
    <div class="filter-options-container">
        <div class="filter-options" id="game-filters">
            <button class="filter-pill" data-filter="game" data-value="lol">League of Legends</button>
            <button class="filter-pill" data-filter="game" data-value="valorant">Valorant</button>
            <button class="filter-pill" data-filter="game" data-value="csgo">CS:GO</button>
            <button class="filter-pill" data-filter="game" data-value="dota">Dota 2</button>
        </div>
        
        <div class="filter-options" id="format-filters">
            <button class="filter-pill" data-filter="format" data-value="single">Single Elimination</button>
            <button class="filter-pill" data-filter="format" data-value="double">Double Elimination</button>
            <button class="filter-pill" data-filter="format" data-value="round">Round Robin</button>
            <button class="filter-pill" data-filter="format" data-value="swiss">Swiss System</button>
        </div>
        
        <div class="filter-options" id="level-filters">
            <button class="filter-pill" data-filter="level" data-value="beginner">Beginner</button>
            <button class="filter-pill" data-filter="level" data-value="intermediate">Intermediate</button>
            <button class="filter-pill" data-filter="level" data-value="advanced">Advanced</button>
            <button class="filter-pill" data-filter="level" data-value="pro">Professional</button>
        </div>
        
        <div class="filter-options" id="region-filters">
            <button class="filter-pill" data-filter="region" data-value="na">North America</button>
            <button class="filter-pill" data-filter="region" data-value="eu">Europe</button>
            <button class="filter-pill" data-filter="region" data-value="sa">South America</button>
            <button class="filter-pill" data-filter="region" data-value="asia">Asia</button>
        </div>
    </div>
    
    <!-- Debug: mostrar filtros activos -->
    <div class="active-filters-debug" style="margin-top: 20px; padding: 15px; background: var(--background-card); border-radius: 10px; text-align: center;">
        <p style="color: var(--primary-text); margin: 0 0 10px 0;">Filtros activos:</p>
        <div id="active-filters-display" style="color: var(--secondary-text); font-size: 14px;"></div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const filterTitles = document.querySelectorAll('.filter-title');
    const filterOptions = document.querySelectorAll('.filter-options');
    const filterPills = document.querySelectorAll('.filter-pill');
    
    // Array para guardar los filtros activos
    let activeFilters = {};
    
    // Inicialmente ocultar todas las opciones
    filterOptions.forEach(option => {
        option.style.display = 'none';
    });
    
    // Función para actualizar la visualización de filtros activos
    function updateActiveFiltersDisplay() {
        const display = document.getElementById('active-filters-display');
        if (Object.keys(activeFilters).length === 0) {
            display.innerHTML = '<em>Ningún filtro activo</em>';
        } else {
            let html = '';
            for (const [filterType, values] of Object.entries(activeFilters)) {
                html += `<strong>${filterType}:</strong> ${values.join(', ')}<br>`;
            }
            display.innerHTML = html;
        }
    }
    
    // Función para actualizar el estado visual de los pills
    function updatePillVisualState() {
        filterPills.forEach(pill => {
            const filterType = pill.dataset.filter;
            const filterValue = pill.dataset.value;
            
            if (activeFilters[filterType] && activeFilters[filterType].includes(filterValue)) {
                pill.classList.add('active');
            } else {
                pill.classList.remove('active');
            }
        });
    }
    
    // Event listeners para los títulos (expandir/contraer)
    filterTitles.forEach(title => {
        title.addEventListener('click', function() {
            const targetId = this.dataset.target;
            const targetOptions = document.getElementById(targetId);
            const arrow = this.querySelector('.filter-arrow');
            
            // Cerrar todas las otras opciones
            filterOptions.forEach(option => {
                if (option.id !== targetId) {
                    option.style.display = 'none';
                    // Resetear flechas de otros títulos
                    const otherTitle = document.querySelector(`[data-target="${option.id}"]`);
                    if (otherTitle) {
                        otherTitle.querySelector('.filter-arrow').textContent = '▼';
                    }
                }
            });
            
            // Toggle del target actual
            if (targetOptions.style.display === 'none') {
                targetOptions.style.display = 'flex';
                arrow.textContent = '▲';
            } else {
                targetOptions.style.display = 'none';
                arrow.textContent = '▼';
            }
        });
    });
    
    // Event listeners para los pills (seleccionar/deseleccionar)
    filterPills.forEach(pill => {
        pill.addEventListener('click', function() {
            const filterType = this.dataset.filter;
            const filterValue = this.dataset.value;
            
            // Toggle del estado activo
            if (!activeFilters[filterType]) {
                activeFilters[filterType] = [];
            }
            
            const valueIndex = activeFilters[filterType].indexOf(filterValue);
            if (valueIndex > -1) {
                // Remover filtro
                activeFilters[filterType].splice(valueIndex, 1);
                if (activeFilters[filterType].length === 0) {
                    delete activeFilters[filterType];
                }
            } else {
                // Agregar filtro
                activeFilters[filterType].push(filterValue);
            }
            
            // Actualizar visualización
            updateActiveFiltersDisplay();
            updatePillVisualState();
            
            console.log('Filtros activos:', activeFilters);
        });
    });
    
    // Inicializar visualización
    updateActiveFiltersDisplay();
});
</script>

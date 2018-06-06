/**
 * 
 */

var areadata ={ 
    province : [], 
    city: [], 
    district: [] 
    } 
function initAreaData(){ 
    var dataroot="/scripts/area/areas.json"; 
    $.getJSON(dataroot, function(data){ 
        areadata.province=data.province; 
        areadata.city=data.city; 
        areadata.district=data.district; 
    }); 
} 


 {
             text: '<i class="add_icon  icon_c"></i>',
             action: function ( e, dt, node, config ) {
                 fire_ajax_submit("/admin/role/add");
             }
         },
         {
              text: '<i class="edit_icon  icon_c"></i>',
              action: function ( e, dt, node, config ) {
                  if( $("#roleId").val() ) {
                    fire_ajax_submit("/admin/role/edit?id="+$("#roleId").val());
                  } else {
                    alert('please select the role you wanted to edit! ');
                  }
              }
          },
          {
               text: '<button class="btn btn-primary btn-icon" type="button">Save</button>',
               action: function ( e, dt, node, config ) {
                   dt.ajax.reload();
               }
           },
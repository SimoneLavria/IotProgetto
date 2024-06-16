import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'FrontEnd';
  current_val='1'

  getval(value: string) {
    console.warn(value)
    this.current_val=value
    let scr_inziale='https://charts.mongodb.com/charts-project-0-qldul/embed/dashboards?id=65deac0d-4859-420f-8b9e-8e9bcc0e9e9b&theme=light&autoRefresh=true&maxDataAge=1800&showTitleAndDesc=true&scalingWidth=scale&scalingHeight=scale&'
    let calendar=document.getElementById("frame_id")

    if(value=='all')
    {
      // @ts-ignore
      calendar.setAttribute("src",scr_inziale)
    }
    // @ts-ignore
    else
    {
      let scr2='filter={"id_trattore":%20{$eq:%20'
      let value_da_inserire=value
      let parte_finale='}}'
      let nuovo_scr=scr_inziale.concat(scr2,value_da_inserire,parte_finale)
      // @ts-ignore
      calendar.setAttribute("src", nuovo_scr);
    }
  }
}

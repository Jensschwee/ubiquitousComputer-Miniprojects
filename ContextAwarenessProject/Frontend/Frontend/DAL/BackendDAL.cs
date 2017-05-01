using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using Frontend.Helpers;
using Frontend.Model;
using Newtonsoft.Json;

namespace Frontend.DAL
{
    public class BackendDAL
    {
        public async Task<List<BoardInOut>> GetBoardData()
        {
            using (var client = new HttpClient())
            {
                client.BaseAddress = new Uri(Backend.GetBackendBaseAdress() + "board/data");
                HttpResponseMessage response = await client.GetAsync("");   
                if (response.IsSuccessStatusCode)
                {
                    string responseBody = await response.Content.ReadAsStringAsync();
                    var board = JsonConvert.DeserializeObject<List<BoardInOut>>(responseBody);
                    if (board != null)
                    {
                        return board;
                    }
                }
                return null;
            }
        }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Frontend.Model
{
    public class BoardInOut
    {
        public string deviceId { get; set; }
        public string username { get; set; }
        public string status { get; set; }
        public string location { get; set; }
        public string lastSeen { get; set; }
    }
}

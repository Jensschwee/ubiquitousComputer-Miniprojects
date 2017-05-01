using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Frontend.DAL;
using Microsoft.AspNetCore.Mvc;

namespace Frontend.Controllers
{
    public class HomeController : Controller
    {
        BackendDAL dal = new BackendDAL();

        public IActionResult Index()
        {
            ViewData["title"] = "Find your colleague";
            var boardData = dal.GetBoardData().Result.OrderBy(eq => eq.username).ThenBy(board => board.status).ToList();
            return View(boardData);
        }

        public IActionResult Error()
        {
            return View();
        }
    }
}

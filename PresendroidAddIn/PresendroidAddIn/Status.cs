using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace PresendroidAddIn
{
    public partial class Status : Form
    {
        public Status()
        {
            InitializeComponent();
        }

        public void Append(string text)
        {
            textBox1.AppendText(DateTime.Now.ToShortTimeString() + ": " + text + Environment.NewLine);
        }
    }
}

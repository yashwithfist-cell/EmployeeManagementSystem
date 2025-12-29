import React, { useEffect, useState } from 'react';
import api from '../utils/api';
const Departments = () => {
  const [departments, setDepartments] = useState([]);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [newDepartment, setNewDepartment] = useState({
    departmentName: '',
  });
  const [editDepartment, setEditDepartment] = useState(null);
  const [error, setError] = useState('');
  const [editError, setEditError] = useState('');

  useEffect(() => {
    api.get('/departments')
      .then(response => {
        setDepartments(response.data);
      })
      .catch(error => {
        console.error('Error fetching departments:', error);
      });
  }, []);

  const handleAddDepartmentChange = (e) => {
    const { name, value } = e.target;
    setNewDepartment(prev => ({ ...prev, [name]: value }));
  };

  const handleAddDepartmentSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const res = await api.post('/departments', {
        departmentName: newDepartment.departmentName,
      });
      setDepartments(prev => [...prev, res.data]);
      setShowAddModal(false);
      setNewDepartment({ departmentName: '' });
    } catch (err) {
      setError('Failed to add department. Please check your input.');
    }
  };

  const handleEdit = (department) => {
    setEditDepartment({ ...department });
    setShowEditModal(true);
    setEditError('');
  };

  const handleEditDepartmentChange = (e) => {
    const { name, value } = e.target;
    setEditDepartment(prev => ({ ...prev, [name]: value }));
  };

  const handleEditDepartmentSubmit = async (e) => {
    e.preventDefault();
    setEditError('');
    try {
      await api.put(`/departments/${editDepartment.departmentId}`, {
        departmentName: editDepartment.departmentName,
      });
      setDepartments(prev => prev.map(d => d.departmentId === editDepartment.departmentId ? editDepartment : d));
      setShowEditModal(false);
      setEditDepartment(null);
    } catch (err) {
      setEditError('Failed to update department. Please check your input.');
    }
  };

  const handleDelete = async (departmentId) => {
    if (window.confirm('Are you sure you want to delete this department?')) {
      try {
        await api.delete(`/departments/${departmentId}`);
        setDepartments(prev => prev.filter(d => d.departmentId !== departmentId));
      } catch (err) {
        alert('Failed to delete department.');
      }
    }
  };

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="max-w-4xl mx-auto bg-white rounded-lg shadow-xl p-6">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-3xl font-extrabold text-gray-800">All Departments</h2>
          <button
            className="bg-green-600 hover:bg-green-700 text-white font-semibold px-5 py-2.5 rounded-lg shadow-md transition duration-300 ease-in-out transform hover:scale-105 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-opacity-50"
            onClick={() => setShowAddModal(true)}
          >
            Add Department
          </button>
        </div>

        {/* Add Department Modal */}
        {showAddModal && (
          <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50 p-4">
            <div className="bg-white p-8 rounded-xl shadow-2xl w-full max-w-md animate-fade-in-down">
              <h3 className="text-2xl font-bold text-gray-800 mb-6 text-center">Add New Department</h3>
              <form onSubmit={handleAddDepartmentSubmit} className="space-y-5">
                <input
                  type="text"
                  name="departmentName"
                  placeholder="Department Name"
                  value={newDepartment.departmentName}
                  onChange={handleAddDepartmentChange}
                  className="w-full border border-gray-300 rounded-lg px-4 py-3 text-lg focus:ring-2 focus:ring-green-500 focus:border-transparent transition duration-200"
                  required
                />
                {error && <div className="text-red-600 text-sm mt-1">{error}</div>}
                <div className="flex justify-end gap-3 pt-2">
                  <button
                    type="button"
                    className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold px-5 py-2.5 rounded-lg shadow-sm transition duration-300 ease-in-out focus:outline-none focus:ring-2 focus:ring-gray-400 focus:ring-opacity-50"
                    onClick={() => {
                      setShowAddModal(false);
                      setError('');
                      setNewDepartment({ departmentName: '' }); // Clear input on cancel
                    }}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="bg-green-600 hover:bg-green-700 text-white font-semibold px-5 py-2.5 rounded-lg shadow-md transition duration-300 ease-in-out transform hover:scale-105 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-opacity-50"
                  >
                    Add
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* Edit Department Modal */}
        {showEditModal && editDepartment && (
          <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50 p-4">
            <div className="bg-white p-8 rounded-xl shadow-2xl w-full max-w-md animate-fade-in-down">
              <h3 className="text-2xl font-bold text-gray-800 mb-6 text-center">Edit Department</h3>
              <form onSubmit={handleEditDepartmentSubmit} className="space-y-5">
                <input
                  type="text"
                  name="departmentName"
                  placeholder="Department Name"
                  value={editDepartment.departmentName}
                  onChange={handleEditDepartmentChange}
                  className="w-full border border-gray-300 rounded-lg px-4 py-3 text-lg focus:ring-2 focus:ring-yellow-500 focus:border-transparent transition duration-200"
                  required
                />
                {editError && <div className="text-red-600 text-sm mt-1">{editError}</div>}
                <div className="flex justify-end gap-3 pt-2">
                  <button
                    type="button"
                    className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold px-5 py-2.5 rounded-lg shadow-sm transition duration-300 ease-in-out focus:outline-none focus:ring-2 focus:ring-gray-400 focus:ring-opacity-50"
                    onClick={() => {
                      setShowEditModal(false);
                      setEditError('');
                      setEditDepartment(null); // Clear edited department on cancel
                    }}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="bg-yellow-500 hover:bg-yellow-600 text-white font-semibold px-5 py-2.5 rounded-lg shadow-md transition duration-300 ease-in-out transform hover:scale-105 focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:ring-opacity-50"
                  >
                    Save Changes
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        <div className="overflow-x-auto rounded-lg shadow-md border border-gray-200">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-100">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Department ID</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Department Name</th>
                <th className="px-6 py-3 text-center text-xs font-semibold text-gray-600 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {Array.isArray(departments) && departments.length > 0 ? (
                departments.map((department, idx) => (
                  <tr
                    key={department.departmentId}
                    className={idx % 2 === 0 ? "bg-white" : "bg-gray-50 hover:bg-gray-100 transition duration-150 ease-in-out"}
                  >
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{department.departmentId}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{department.departmentName}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      <div className="flex justify-center gap-3">
                        <button
                          className="bg-yellow-500 hover:bg-yellow-600 text-white font-medium px-4 py-2 rounded-lg shadow-sm transition duration-200 ease-in-out transform hover:scale-105 focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-opacity-50 min-w-[70px]"
                          onClick={() => handleEdit(department)}
                        >
                          Edit
                        </button>
                        <button
                          className="bg-red-600 hover:bg-red-700 text-white font-medium px-4 py-2 rounded-lg shadow-sm transition duration-200 ease-in-out transform hover:scale-105 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-opacity-50 min-w-[70px]"
                          onClick={() => handleDelete(department.departmentId)}
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="3" className="px-6 py-4 text-center text-gray-500 text-lg">
                    No departments found. Add a new department to get started!
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Departments;
